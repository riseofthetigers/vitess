// Copyright 2016, Google Inc. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package tabletserver

import (
	"errors"
	"fmt"
	"sync"
)

// TxPreparedPool manages connections for prepared transactions.
// The Prepare functionality and associated orchestration
// is done by TxPool.
type TxPreparedPool struct {
	mu       sync.Mutex
	conns    map[string]*TxConnection
	capacity int
}

// NewTxPreparedPool creates a new TxPreparedPool.
func NewTxPreparedPool(capacity int) *TxPreparedPool {
	return &TxPreparedPool{
		conns:    make(map[string]*TxConnection, capacity),
		capacity: capacity,
	}
}

// Put adds the connection to the pool. It returns an error
// if the pool is full, and panics on duplicate key.
func (pp *TxPreparedPool) Put(c *TxConnection, dtid string) error {
	pp.mu.Lock()
	defer pp.mu.Unlock()
	if _, ok := pp.conns[dtid]; ok {
		return errors.New("duplicate DTID in Prepare: " + dtid)
	}
	if len(pp.conns) >= pp.capacity {
		return fmt.Errorf("prepared transactions exceeded limit: %d", pp.capacity)
	}
	pp.conns[dtid] = c
	return nil
}

// Get returns the connection and removes it from the pool.
// If the connection is not found, it returns nil.
func (pp *TxPreparedPool) Get(dtid string) *TxConnection {
	pp.mu.Lock()
	defer pp.mu.Unlock()
	c := pp.conns[dtid]
	delete(pp.conns, dtid)
	return c
}

// GetAll removes all connections and returns them as a list.
func (pp *TxPreparedPool) GetAll() []*TxConnection {
	pp.mu.Lock()
	defer pp.mu.Unlock()
	conns := make([]*TxConnection, 0, len(pp.conns))
	for _, c := range pp.conns {
		conns = append(conns, c)
	}
	pp.conns = make(map[string]*TxConnection, pp.capacity)
	return conns
}
