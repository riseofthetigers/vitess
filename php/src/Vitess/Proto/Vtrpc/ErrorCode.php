<?php
// DO NOT EDIT! Generated by Protobuf-PHP protoc plugin 1.0
// Source: vtrpc.proto

namespace Vitess\Proto\Vtrpc {

  class ErrorCode extends \DrSlump\Protobuf\Enum {
    const SUCCESS = 0;
    const CANCELLED = 1;
    const UNKNOWN_ERROR = 2;
    const BAD_INPUT = 3;
    const DEADLINE_EXCEEDED = 4;
    const INTEGRITY_ERROR = 5;
    const PERMISSION_DENIED = 6;
    const RESOURCE_EXHAUSTED = 7;
    const QUERY_NOT_SERVED = 8;
    const NOT_IN_TX = 9;
    const INTERNAL_ERROR = 10;
    const TRANSIENT_ERROR = 11;
    const UNAUTHENTICATED = 12;
  }
}
