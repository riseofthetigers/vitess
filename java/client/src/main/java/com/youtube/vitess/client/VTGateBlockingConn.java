package com.youtube.vitess.client;

import com.youtube.vitess.client.cursor.Cursor;
import com.youtube.vitess.proto.Topodata.KeyRange;
import com.youtube.vitess.proto.Topodata.SrvKeyspace;
import com.youtube.vitess.proto.Topodata.TabletType;
import com.youtube.vitess.proto.Vtgate.BoundKeyspaceIdQuery;
import com.youtube.vitess.proto.Vtgate.BoundShardQuery;
import com.youtube.vitess.proto.Vtgate.SplitQueryResponse;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * A synchronous wrapper around a VTGate connection.
 *
 * <p>
 * This is a wrapper around the asynchronous {@link VTGateConn} class that converts all methods to
 * synchronous.
 */
public class VTGateBlockingConn implements Closeable {
  private final VTGateConn conn;

  /**
   * Creates a new {@link VTGateConn} with the given {@link RpcClient} and wraps it in a synchronous
   * API.
   */
  public VTGateBlockingConn(RpcClient client) {
    conn = new VTGateConn(client);
  }

  /**
   * Creates a new {@link VTGateConn} with the given {@link RpcClient} and wraps it in a synchronous
   * API.
   */
  public VTGateBlockingConn(RpcClient client, String keyspace) {
    conn = new VTGateConn(client, keyspace);
  }

  /**
   * Wraps an existing {@link VTGateConn} in a synchronous API.
   */
  public VTGateBlockingConn(VTGateConn conn) {
    this.conn = conn;
  }

  public Cursor execute(Context ctx, String query, Map<String, ?> bindVars, TabletType tabletType)
      throws SQLException {
    return conn.execute(ctx, query, bindVars, tabletType).checkedGet();
  }

  public Cursor executeShards(Context ctx, String query, String keyspace, Iterable<String> shards,
      Map<String, ?> bindVars, TabletType tabletType) throws SQLException {
    return conn.executeShards(ctx, query, keyspace, shards, bindVars, tabletType).checkedGet();
  }

  public Cursor executeKeyspaceIds(Context ctx, String query, String keyspace,
      Iterable<byte[]> keyspaceIds, Map<String, ?> bindVars, TabletType tabletType)
      throws SQLException {
    return conn.executeKeyspaceIds(ctx, query, keyspace, keyspaceIds, bindVars, tabletType)
        .checkedGet();
  }

  public Cursor executeKeyRanges(Context ctx, String query, String keyspace,
      Iterable<? extends KeyRange> keyRanges, Map<String, ?> bindVars, TabletType tabletType)
      throws SQLException {
    return conn.executeKeyRanges(ctx, query, keyspace, keyRanges, bindVars, tabletType)
        .checkedGet();
  }

  public Cursor executeEntityIds(Context ctx, String query, String keyspace,
      String entityColumnName, Map<byte[], ?> entityKeyspaceIds, Map<String, ?> bindVars,
      TabletType tabletType) throws SQLException {
    return conn.executeEntityIds(ctx, query, keyspace, entityColumnName, entityKeyspaceIds,
        bindVars, tabletType).checkedGet();
  }

  /**
   * Execute multiple keyspace ID queries as a batch.
   *
   * @param asTransaction If true, automatically create a transaction (per shard) that encloses all
   *        the batch queries.
   */
  public List<Cursor> executeBatchShards(Context ctx, Iterable<? extends BoundShardQuery> queries,
      TabletType tabletType, boolean asTransaction) throws SQLException {
    return conn.executeBatchShards(ctx, queries, tabletType, asTransaction).checkedGet();
  }

  /**
   * Execute multiple keyspace ID queries as a batch.
   *
   * @param asTransaction If true, automatically create a transaction (per shard) that encloses all
   *        the batch queries.
   */
  public List<Cursor> executeBatchKeyspaceIds(Context ctx,
      Iterable<? extends BoundKeyspaceIdQuery> queries, TabletType tabletType,
      boolean asTransaction) throws SQLException {
    return conn.executeBatchKeyspaceIds(ctx, queries, tabletType, asTransaction).checkedGet();
  }

  public Cursor streamExecute(Context ctx, String query, Map<String, ?> bindVars,
      TabletType tabletType) throws SQLException {
    return conn.streamExecute(ctx, query, bindVars, tabletType);
  }

  public Cursor streamExecuteShards(Context ctx, String query, String keyspace,
      Iterable<String> shards, Map<String, ?> bindVars, TabletType tabletType) throws SQLException {
    return conn.streamExecuteShards(ctx, query, keyspace, shards, bindVars, tabletType);
  }

  public Cursor streamExecuteKeyspaceIds(Context ctx, String query, String keyspace,
      Iterable<byte[]> keyspaceIds, Map<String, ?> bindVars, TabletType tabletType)
      throws SQLException {
    return conn.streamExecuteKeyspaceIds(ctx, query, keyspace, keyspaceIds, bindVars, tabletType);
  }

  public Cursor streamExecuteKeyRanges(Context ctx, String query, String keyspace,
      Iterable<? extends KeyRange> keyRanges, Map<String, ?> bindVars, TabletType tabletType)
      throws SQLException {
    return conn.streamExecuteKeyRanges(ctx, query, keyspace, keyRanges, bindVars, tabletType);
  }

  public VTGateBlockingTx begin(Context ctx) throws SQLException {
    return new VTGateBlockingTx(conn.begin(ctx).checkedGet());
  }

  public List<SplitQueryResponse.Part> splitQuery(Context ctx, String keyspace, String query,
      Map<String, ?> bindVars, String splitColumn, long splitCount) throws SQLException {
    return conn.splitQuery(ctx, keyspace, query, bindVars, splitColumn, splitCount).checkedGet();
  }

  public SrvKeyspace getSrvKeyspace(Context ctx, String keyspace) throws SQLException {
    return conn.getSrvKeyspace(ctx, keyspace).checkedGet();
  }

  @Override
  public void close() throws IOException {
    conn.close();
  }
}
