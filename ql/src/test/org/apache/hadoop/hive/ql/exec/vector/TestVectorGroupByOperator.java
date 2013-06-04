/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.exec.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hive.ql.exec.vector.util.FakeCaptureOutputOperator;
import org.apache.hadoop.hive.ql.exec.vector.util.FakeVectorRowBatchFromConcat;
import org.apache.hadoop.hive.ql.exec.vector.util.FakeVectorRowBatchFromLongIterables;
import org.apache.hadoop.hive.ql.exec.vector.util.FakeVectorRowBatchFromRepeats;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.AggregationDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.GroupByDesc;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for the vectorized GROUP BY operator.
 */
public class TestVectorGroupByOperator {

  private static ExprNodeDesc buildColumnDesc(
      VectorizationContext ctx,
      String column,
      TypeInfo typeInfo) {

    return new ExprNodeColumnDesc(
        typeInfo, column, "table", false);
  }

  private static AggregationDesc buildAggregationDesc(
      VectorizationContext ctx,
      String aggregate,
      String column,
      TypeInfo typeInfo) {

    ExprNodeDesc inputColumn = buildColumnDesc(ctx, column, typeInfo);

    ArrayList<ExprNodeDesc> params = new ArrayList<ExprNodeDesc>();
    params.add(inputColumn);

    AggregationDesc agg = new AggregationDesc();
    agg.setGenericUDAFName(aggregate);
    agg.setParameters(params);

    return agg;
  }
  private static AggregationDesc buildAggregationDescCountStar(
      VectorizationContext ctx) {
    AggregationDesc agg = new AggregationDesc();
    agg.setGenericUDAFName("COUNT");
    agg.setParameters(new ArrayList<ExprNodeDesc>());
    return agg;
  }


  private static GroupByDesc buildGroupByDescType(
      VectorizationContext ctx,
      String aggregate,
      String column,
      TypeInfo dataType) {

    AggregationDesc agg = buildAggregationDesc(ctx, aggregate,
        column, dataType);
    ArrayList<AggregationDesc> aggs = new ArrayList<AggregationDesc>();
    aggs.add(agg);

    ArrayList<String> outputColumnNames = new ArrayList<String>();
    outputColumnNames.add("_col0");

    GroupByDesc desc = new GroupByDesc();
    desc.setOutputColumnNames(outputColumnNames);
    desc.setAggregators(aggs);

    return desc;
  }

  private static GroupByDesc buildGroupByDescCountStar(
      VectorizationContext ctx) {

    AggregationDesc agg = buildAggregationDescCountStar(ctx);
    ArrayList<AggregationDesc> aggs = new ArrayList<AggregationDesc>();
    aggs.add(agg);

    ArrayList<String> outputColumnNames = new ArrayList<String>();
    outputColumnNames.add("_col0");

    GroupByDesc desc = new GroupByDesc();
    desc.setOutputColumnNames(outputColumnNames);
    desc.setAggregators(aggs);

    return desc;
  }


  private static GroupByDesc buildKeyGroupByDesc(
      VectorizationContext ctx,
      String aggregate,
      String column,
      TypeInfo dataTypeInfo,
      String key,
      TypeInfo keyTypeInfo) {

    GroupByDesc desc = buildGroupByDescType(ctx, aggregate, column, dataTypeInfo);

    ExprNodeDesc keyExp = buildColumnDesc(ctx, key, keyTypeInfo);
    ArrayList<ExprNodeDesc> keys = new ArrayList<ExprNodeDesc>();
    keys.add(keyExp);
    desc.setKeys(keys);
    
    desc.getOutputColumnNames().add("_col1");

    return desc;
  }
  
  @Test
  public void testDoubleValueTypeSum() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 20.0, null, 19.0));
  }
  
  @Test
  public void testDoubleValueTypeSumOneKey() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1, 1, 1, 1}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 39.0));
  }  
  
  @Test
  public void testDoubleValueTypeCount() throws HiveException {
    testKeyTypeAggregate(
        "count",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 2L, null, 1L));
  }
  
  public void testDoubleValueTypeCountOneKey() throws HiveException {
    testKeyTypeAggregate(
        "count",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1, 1, 1, 1}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 3L));
  }  
  
  @Test
  public void testDoubleValueTypeAvg() throws HiveException {
    testKeyTypeAggregate(
        "avg",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 10.0, null, 19.0));
  }
  
  @Test
  public void testDoubleValueTypeAvgOneKey() throws HiveException {
    testKeyTypeAggregate(
        "avg",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1, 1, 1, 1}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 13.0));
  }  
  
  @Test
  public void testDoubleValueTypeMin() throws HiveException {
    testKeyTypeAggregate(
        "min",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 7.0, null, 19.0));
  }
  
  @Test
  public void testDoubleValueTypeMinOneKey() throws HiveException {
    testKeyTypeAggregate(
        "min",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1, 1, 1, 1}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 7.0));
  }
  
  @Test
  public void testDoubleValueTypeMax() throws HiveException {
    testKeyTypeAggregate(
        "max",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 13.0, null, 19.0));
  }

  @Test
  public void testDoubleValueTypeMaxOneKey() throws HiveException {
    testKeyTypeAggregate(
        "max",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1, 1, 1, 1}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 19.0));
  }

  @Test
  public void testDoubleValueTypeVariance() throws HiveException {
    testKeyTypeAggregate(
        "variance",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 9.0, null, 0.0));
  }
  
  @Test
  public void testDoubleValueTypeVarianceOneKey() throws HiveException {
    testKeyTypeAggregate(
        "variance",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "double"},
            Arrays.asList(new Object[]{  1, 1, 1, 1}),
            Arrays.asList(new Object[]{13.0,null,7.0, 19.0})),
        buildHashMap((byte)1, 24.0));
  }  
  @Test
  public void testTinyintKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"tinyint", "bigint"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap((byte)1, 20L, null, 19L));
  }
  
  @Test
  public void testSmallintKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"smallint", "bigint"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap((short)1, 20L, null, 19L));
  }  
  
  @Test
  public void testIntKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"int", "bigint"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap((int)1, 20L, null, 19L));
  }
  
  @Test
  public void testBigintKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"bigint", "bigint"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap((long)1L, 20L, null, 19L));
  }
  
  @Test
  public void testBooleanKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"boolean", "bigint"},
            Arrays.asList(new Object[]{  true,null, true, null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap(true, 20L, null, 19L));
  }
  
  @Test
  public void testTimestampKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"timestamp", "bigint"},
            Arrays.asList(new Object[]{new Timestamp(1),null, new Timestamp(1), null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap(new Timestamp(1), 20L, null, 19L));
  }  
  
  @Test
  public void testFloatKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"float", "bigint"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap((float)1.0, 20L, null, 19L));
  }
  
  @Test
  public void testDoubleKeyTypeAggregate() throws HiveException {
    testKeyTypeAggregate(
        "sum",
        new FakeVectorRowBatchFromObjectIterables(
            2,
            new String[] {"double", "bigint"},
            Arrays.asList(new Object[]{  1,null, 1, null}),
            Arrays.asList(new Object[]{13L,null,7L, 19L})),
        buildHashMap((double)1.0, 20L, null, 19L));
  }    
  
  @Test
  public void testCountStar() throws HiveException {
    testAggregateCountStar(
        2,
        Arrays.asList(new Long[]{13L,null,7L,19L}),
        4L);
  }

  @Test
  public void testCountString() throws HiveException {
    testAggregateString(
        "count",
        2,
        Arrays.asList(new Object[]{"A","B","C"}),
        3L);
  }

  @Test
  public void testMaxString() throws HiveException {
    testAggregateString(
        "max",
        2,
        Arrays.asList(new Object[]{"A","B","C"}),
        "C");
    testAggregateString(
        "max",
        2,
        Arrays.asList(new Object[]{"C", "B", "A"}),
        "C");
  }

  @Test
  public void testMinString() throws HiveException {
    testAggregateString(
        "min",
        2,
        Arrays.asList(new Object[]{"A","B","C"}),
        "A");
    testAggregateString(
        "min",
        2,
        Arrays.asList(new Object[]{"C", "B", "A"}),
        "A");
  }

  @Test
  public void testMaxNullString() throws HiveException {
    testAggregateString(
        "max",
        2,
        Arrays.asList(new Object[]{"A","B",null}),
        "B");
    testAggregateString(
        "max",
        2,
        Arrays.asList(new Object[]{null, null, null}),
        null);
  }

  @Test
  public void testCountStringWithNull() throws HiveException {
    testAggregateString(
        "count",
        2,
        Arrays.asList(new Object[]{"A",null,"C", "D", null}),
        3L);
  }

  @Test
  public void testCountStringAllNull() throws HiveException {
    testAggregateString(
        "count",
        4,
        Arrays.asList(new Object[]{null, null, null, null, null}),
        0L);
  }


  @Test
  public void testMinLongNullStringKeys() throws HiveException {
    testAggregateStringKeyAggregate(
        "min",
        2,
        Arrays.asList(new Object[]{"A",null,"A",null}),
        Arrays.asList(new Object[]{13L, 5L, 7L,19L}),
        buildHashMap("A", 7L, null, 5L));
  }

  @Test
  public void testMinLongStringKeys() throws HiveException {
    testAggregateStringKeyAggregate(
        "min",
        2,
        Arrays.asList(new Object[]{"A","B","A","B"}),
        Arrays.asList(new Object[]{13L, 5L, 7L,19L}),
        buildHashMap("A", 7L, "B", 5L));
  }

  @Test
  public void testMinLongKeyGroupByCompactBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{01L,1L,2L,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(1L, 5L, 2L, 7L));
  }

  @Test
  public void testMinLongKeyGroupBySingleBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "min",
        4,
        Arrays.asList(new Long[]{01L,1L,2L,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(1L, 5L, 2L, 7L));
  }

  @Test
  public void testMinLongKeyGroupByCrossBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{01L,2L,1L,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(1L, 7L, 2L, 5L));
  }

  @Test
  public void testMinLongNullKeyGroupByCrossBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{null,2L,null,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(null, 7L, 2L, 5L));
  }

  @Test
  public void testMinLongNullKeyGroupBySingleBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "min",
        4,
        Arrays.asList(new Long[]{null,2L,null,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(null, 7L, 2L, 5L));
  }

  @Test
  public void testMaxLongNullKeyGroupBySingleBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "max",
        4,
        Arrays.asList(new Long[]{null,2L,null,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(null, 13L, 2L, 19L));
  }

  @Test
  public void testCountLongNullKeyGroupBySingleBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "count",
        4,
        Arrays.asList(new Long[]{null,2L,null,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(null, 2L, 2L, 2L));
  }

  @Test
  public void testSumLongNullKeyGroupBySingleBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "sum",
        4,
        Arrays.asList(new Long[]{null,2L,null,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(null, 20L, 2L, 24L));
  }

  @Test
  public void testAvgLongNullKeyGroupBySingleBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "avg",
        4,
        Arrays.asList(new Long[]{null,2L,null,02L}),
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        buildHashMap(null, 10.0, 2L, 12.0));
  }

  @Test
  public void testVarLongNullKeyGroupBySingleBatch() throws HiveException {
    testAggregateLongKeyAggregate(
        "variance",
        4,
        Arrays.asList(new Long[]{null,2L,01L,02L,01L,01L}),
        Arrays.asList(new Long[]{13L, 5L,18L,19L,12L,15L}),
        buildHashMap(null, 0.0, 2L, 49.0, 01L, 6.0));
  }

  @Test
  public void testMinNullLongNullKeyGroupBy() throws HiveException {
    testAggregateLongKeyAggregate(
        "min",
        4,
        Arrays.asList(new Long[]{null,2L,null,02L}),
        Arrays.asList(new Long[]{null, null, null, null}),
        buildHashMap(null, null, 2L, null));
  }

  @Test
  public void testMinLongGroupBy() throws HiveException {
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        5L);
  }


  @Test
  public void testMinLongSimple() throws HiveException {
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        5L);
  }

  @Test
  public void testMinLongEmpty() throws HiveException {
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }

  @Test
  public void testMinLongNulls() throws HiveException {
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{null}),
        null);
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{null, null, null}),
        null);
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{null,5L,7L,19L}),
        5L);
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{13L,null,7L,19L}),
        7L);
  }

  @Test
  public void testMinLongRepeat () throws HiveException {
    testAggregateLongRepeats (
        "min",
        42L,
        4096,
        1024,
        42L);
  }

  @Test
  public void testMinLongRepeatNulls () throws HiveException {
    testAggregateLongRepeats (
        "min",
        null,
        4096,
        1024,
        null);
  }


  @Test
  public void testMinLongNegative () throws HiveException {
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,-19L}),
        -19L);
  }

  @Test
  public void testMinLongMinInt () throws HiveException {
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{13L,5L,(long)Integer.MIN_VALUE,-19L}),
        (long)Integer.MIN_VALUE);
  }

  @Test
  public void testMinLongMinLong () throws HiveException {
    testAggregateLongAggregate(
        "min",
        2,
        Arrays.asList(new Long[]{13L,5L, Long.MIN_VALUE, (long)Integer.MIN_VALUE}),
        Long.MIN_VALUE);
  }

  @Test
  public void testMaxLongSimple () throws HiveException {
    testAggregateLongAggregate(
        "max",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        19L);
  }

  @Test
  public void testMaxLongEmpty () throws HiveException {
    testAggregateLongAggregate(
        "max",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }


  @Test
  public void testMaxLongNegative () throws HiveException {
    testAggregateLongAggregate(
        "max",
        2,
        Arrays.asList(new Long[]{-13L,-5L,-7L,-19L}),
        -5L);
  }

  @Test
  public void testMaxLongMaxInt () throws HiveException {
    testAggregateLongAggregate(
        "max",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,(long)Integer.MAX_VALUE}),
        (long)Integer.MAX_VALUE);
  }

  @Test
  public void testMaxLongMaxLong () throws HiveException {
    testAggregateLongAggregate(
        "max",
        2,
        Arrays.asList(new Long[]{13L,Long.MAX_VALUE - 1L,Long.MAX_VALUE,(long)Integer.MAX_VALUE}),
        Long.MAX_VALUE);
  }

  @Test
  public void testMaxLongRepeat () throws HiveException {
    testAggregateLongRepeats (
        "max",
        42L,
        4096,
        1024,
        42L);
  }

  @Test
  public void testMaxLongNulls () throws HiveException {
    testAggregateLongRepeats (
        "max",
        null,
        4096,
        1024,
        null);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testMinLongConcatRepeat () throws HiveException {
    testAggregateLongIterable ("min",
        new FakeVectorRowBatchFromConcat(
            new FakeVectorRowBatchFromRepeats(
                new Long[] {19L}, 10, 2),
            new FakeVectorRowBatchFromRepeats(
                new Long[] {7L}, 15, 2),
            new FakeVectorRowBatchFromRepeats(
                new Long[] {19L}, 10, 2)),
         7L);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testMinLongRepeatConcatValues () throws HiveException {
    testAggregateLongIterable ("min",
        new FakeVectorRowBatchFromConcat(
            new FakeVectorRowBatchFromRepeats(
                new Long[] {19L}, 10, 2),
            new FakeVectorRowBatchFromLongIterables(
                3,
                Arrays.asList(new Long[]{13L, 7L, 23L, 29L}))),
         7L);
  }

  @Test
  public void testCountLongSimple () throws HiveException {
    testAggregateLongAggregate(
        "count",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        4L);
  }

  @Test
  public void testCountLongEmpty () throws HiveException {
    testAggregateLongAggregate(
        "count",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }

  @Test
  public void testCountLongNulls () throws HiveException {
    testAggregateLongAggregate(
        "count",
        2,
        Arrays.asList(new Long[]{null}),
        0L);
    testAggregateLongAggregate(
        "count",
        2,
        Arrays.asList(new Long[]{null, null, null}),
        0L);
    testAggregateLongAggregate(
        "count",
        2,
        Arrays.asList(new Long[]{null,5L,7L,19L}),
        3L);
    testAggregateLongAggregate(
        "count",
        2,
        Arrays.asList(new Long[]{13L,null,7L,19L}),
        3L);
  }


  @Test
  public void testCountLongRepeat () throws HiveException {
    testAggregateLongRepeats (
        "count",
        42L,
        4096,
        1024,
        4096L);
  }

  @Test
  public void testCountLongRepeatNulls () throws HiveException {
    testAggregateLongRepeats (
        "count",
        null,
        4096,
        1024,
        0L);
  }


  @SuppressWarnings("unchecked")
  @Test
  public void testCountLongRepeatConcatValues () throws HiveException {
    testAggregateLongIterable ("count",
        new FakeVectorRowBatchFromConcat(
            new FakeVectorRowBatchFromRepeats(
                new Long[] {19L}, 10, 2),
            new FakeVectorRowBatchFromLongIterables(
                3,
                Arrays.asList(new Long[]{13L, 7L, 23L, 29L}))),
         14L);
  }

  @Test
  public void testSumDoubleSimple() throws HiveException {
    testAggregateDouble(
        "sum",
        2,
        Arrays.asList(new Object[]{13.0,5.0,7.0,19.0}),
        13.0 + 5.0 + 7.0 + 19.0);
  }

  @Test
  public void testSumDoubleGroupByString() throws HiveException {
    testAggregateDoubleStringKeyAggregate(
        "sum",
        4,
        Arrays.asList(new Object[]{"A", null, "A", null}),
        Arrays.asList(new Object[]{13.0,5.0,7.0,19.0}),
        buildHashMap("A", 20.0, null, 24.0));
  }

  @Test
  public void testSumLongSimple () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        13L + 5L + 7L + 19L);
  }

  @Test
  public void testSumLongEmpty () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }

  @Test
  public void testSumLongNulls () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{null}),
        null);
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{null, null, null}),
        null);
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{null,5L,7L,19L}),
        5L + 7L + 19L);
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{13L,null,7L,19L}),
        13L + 7L + 19L);
  }

  @Test
  public void testSumLongRepeat () throws HiveException {
    testAggregateLongRepeats (
        "sum",
        42L,
        4096,
        1024,
        4096L * 42L);
  }

  @Test
  public void testSumLongRepeatNulls () throws HiveException {
    testAggregateLongRepeats (
        "sum",
        null,
        4096,
        1024,
        null);
  }


  @SuppressWarnings("unchecked")
  @Test
  public void testSumLongRepeatConcatValues () throws HiveException {
    testAggregateLongIterable ("sum",
        new FakeVectorRowBatchFromConcat(
            new FakeVectorRowBatchFromRepeats(
                new Long[] {19L}, 10, 2),
            new FakeVectorRowBatchFromLongIterables(
                3,
                Arrays.asList(new Long[]{13L, 7L, 23L, 29L}))),
         19L*10L + 13L + 7L + 23L +29L);
  }

  @Test
  public void testSumLongZero () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{-(long)Integer.MAX_VALUE, (long)Integer.MAX_VALUE}),
        0L);
  }

  @Test
  public void testSumLong2MaxInt () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{(long)Integer.MAX_VALUE, (long)Integer.MAX_VALUE}),
        4294967294L);
  }

  @Test
  public void testSumLong2MinInt () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{(long)Integer.MIN_VALUE, (long)Integer.MIN_VALUE}),
        -4294967296L);
  }

  @Test
  public void testSumLong2MaxLong () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{Long.MAX_VALUE, Long.MAX_VALUE}),
        -2L); // silent overflow
  }

  @Test
  public void testSumLong2MinLong () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{Long.MIN_VALUE, Long.MIN_VALUE}),
        0L); // silent overflow
  }

  @Test
  public void testSumLongMinMaxLong () throws HiveException {
    testAggregateLongAggregate(
        "sum",
        2,
        Arrays.asList(new Long[]{Long.MAX_VALUE, Long.MIN_VALUE}),
        -1L);
  }

  @Test
  public void testAvgLongSimple () throws HiveException {
    testAggregateLongAggregate(
        "avg",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        (double) (13L + 5L + 7L + 19L) / (double) 4L);
  }

  @Test
  public void testAvgLongEmpty () throws HiveException {
    testAggregateLongAggregate(
        "avg",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }

  @Test
  public void testAvgLongNulls () throws HiveException {
    testAggregateLongAggregate(
        "avg",
        2,
        Arrays.asList(new Long[]{null}),
        null);
    testAggregateLongAggregate(
        "avg",
        2,
        Arrays.asList(new Long[]{null, null, null}),
        null);
    testAggregateLongAggregate(
        "avg",
        2,
        Arrays.asList(new Long[]{null,5L,7L,19L}),
        (double) (5L + 7L + 19L) / (double) 3L);
    testAggregateLongAggregate(
        "avg",
        2,
        Arrays.asList(new Long[]{13L,null,7L,19L}),
        (double) (13L + + 7L + 19L) / (double) 3L);
  }


  @Test
  public void testAvgLongRepeat () throws HiveException
  {
    testAggregateLongRepeats (
        "avg",
        42L,
        4096,
        1024,
        (double)42);
  }

  @Test
  public void testAvgLongRepeatNulls () throws HiveException {
    testAggregateLongRepeats (
        "avg",
        null,
        4096,
        1024,
        null);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testAvgLongRepeatConcatValues () throws HiveException {
    testAggregateLongIterable ("avg",
        new FakeVectorRowBatchFromConcat(
            new FakeVectorRowBatchFromRepeats(
                new Long[] {19L}, 10, 2),
            new FakeVectorRowBatchFromLongIterables(
                3,
                Arrays.asList(new Long[]{13L, 7L, 23L, 29L}))),
         (double) (19L*10L + 13L + 7L + 23L +29L) / (double) 14 );
  }

  @Test
  public void testVarianceLongSimple () throws HiveException {
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        (double) 30L);
  }

  @Test
  public void testVarianceLongEmpty () throws HiveException {
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }

  @Test
  public void testVarianceLongSingle () throws HiveException {
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{97L}),
        (double)0.0);
  }

  @Test
  public void testVarianceLongNulls () throws HiveException {
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{null}),
        null);
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{null, null, null}),
        null);
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{null,13L, 5L,7L,19L}),
        (double) 30.0);
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{13L,null,5L, 7L,19L}),
        (double) 30.0);
    testAggregateLongAggregate(
        "variance",
        2,
        Arrays.asList(new Long[]{null,null,null,19L}),
        (double) 0);
  }

  @Test
  public void testVarPopLongRepeatNulls () throws HiveException {
    testAggregateLongRepeats (
        "var_pop",
        null,
        4096,
        1024,
        null);
  }

  @Test
  public void testVarPopLongRepeat () throws HiveException  {
    testAggregateLongRepeats (
        "var_pop",
        42L,
        4096,
        1024,
        (double)0);
  }

  @Test
  public void testVarSampLongSimple () throws HiveException {
    testAggregateLongAggregate(
        "var_samp",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        (double) 40L);
  }

  @Test
  public void testVarSampLongEmpty () throws HiveException {
    testAggregateLongAggregate(
        "var_samp",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }


  @Test
  public void testVarSampLongRepeat () throws HiveException {
    testAggregateLongRepeats (
        "var_samp",
        42L,
        4096,
        1024,
        (double)0);
  }

  @Test
  public void testStdLongSimple () throws HiveException {
    testAggregateLongAggregate(
        "std",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        (double) Math.sqrt(30));
  }

  @Test
  public void testStdLongEmpty () throws HiveException {
    testAggregateLongAggregate(
        "std",
        2,
        Arrays.asList(new Long[]{}),
        null);
  }


  @Test
  public void testStdDevLongRepeat () throws HiveException {
    testAggregateLongRepeats (
        "stddev",
        42L,
        4096,
        1024,
        (double)0);
  }

  @Test
  public void testStdDevLongRepeatNulls () throws HiveException {
    testAggregateLongRepeats (
        "stddev",
        null,
        4096,
        1024,
        null);
  }


  @Test
  public void testStdDevSampSimple () throws HiveException {
    testAggregateLongAggregate(
        "stddev_samp",
        2,
        Arrays.asList(new Long[]{13L,5L,7L,19L}),
        (double) Math.sqrt(40));
  }

  @Test
  public void testStdDevSampLongRepeat () throws HiveException {
    testAggregateLongRepeats (
        "stddev_samp",
        42L,
        3,
        1024,
        (double)0);
  }
  
  private void testKeyTypeAggregate(
      String aggregateName,
      FakeVectorRowBatchFromObjectIterables data,
      Map<Object, Object> expected) throws HiveException {
    
    Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
    mapColumnNames.put("Key", 0);
    mapColumnNames.put("Value", 1);
    VectorizationContext ctx = new VectorizationContext(mapColumnNames, 2);
    Set<Object> keys = new HashSet<Object>();
    
    AggregationDesc agg = buildAggregationDesc(ctx, aggregateName,
        "Value", TypeInfoFactory.getPrimitiveTypeInfo(data.getTypes()[1]));
    ArrayList<AggregationDesc> aggs = new ArrayList<AggregationDesc>();
    aggs.add(agg);

    ArrayList<String> outputColumnNames = new ArrayList<String>();
    outputColumnNames.add("_col0");
    outputColumnNames.add("_col1");

    GroupByDesc desc = new GroupByDesc();
    desc.setOutputColumnNames(outputColumnNames);
    desc.setAggregators(aggs);

    ExprNodeDesc keyExp = buildColumnDesc(ctx, "Key", 
        TypeInfoFactory.getPrimitiveTypeInfo(data.getTypes()[0]));
    ArrayList<ExprNodeDesc> keysDesc = new ArrayList<ExprNodeDesc>();
    keysDesc.add(keyExp);
    desc.setKeys(keysDesc);

    VectorGroupByOperator vgo = new VectorGroupByOperator(ctx, desc);

    FakeCaptureOutputOperator out = FakeCaptureOutputOperator.addCaptureOutputChild(vgo);
    vgo.initialize(null, null);
    out.setOutputInspector(new FakeCaptureOutputOperator.OutputInspector() {

      private int rowIndex;
      private String aggregateName;
      private Map<Object,Object> expected;
      private Set<Object> keys;

      @Override
      public void inspectRow(Object row, int tag) throws HiveException {
        assertTrue(row instanceof Object[]);
        Object[] fields = (Object[]) row;
        assertEquals(2, fields.length);
        Object key = fields[0];
        Object keyValue = null;
        if (null == key) {
          keyValue = null;
        } else if (key instanceof ByteWritable) {
          ByteWritable bwKey = (ByteWritable)key;
          keyValue = bwKey.get();
        } else if (key instanceof ShortWritable) {
          ShortWritable swKey = (ShortWritable)key;
          keyValue = swKey.get();
        } else if (key instanceof IntWritable) {
          IntWritable iwKey = (IntWritable)key;
          keyValue = iwKey.get();
        } else if (key instanceof LongWritable) {
          LongWritable lwKey = (LongWritable)key;
          keyValue = lwKey.get();
        } else if (key instanceof TimestampWritable) {
          TimestampWritable twKey = (TimestampWritable)key;
          keyValue = twKey.getTimestamp();
        } else if (key instanceof DoubleWritable) {
          DoubleWritable dwKey = (DoubleWritable)key;
          keyValue = dwKey.get();
        } else if (key instanceof FloatWritable) {
          FloatWritable fwKey = (FloatWritable)key;
          keyValue = fwKey.get();
        } else if (key instanceof BooleanWritable) {
          BooleanWritable bwKey = (BooleanWritable)key;
          keyValue = bwKey.get();
        } else {
          Assert.fail(String.format("Not implemented key output type %s: %s", 
              key.getClass().getName(), key));
        }
        
        assertTrue(expected.containsKey(keyValue));
        Object expectedValue = expected.get(keyValue);
        Object value = fields[1];
        Validator validator = getValidator(aggregateName);
        validator.validate(expectedValue, new Object[] {value});
        keys.add(keyValue);
      }

      private FakeCaptureOutputOperator.OutputInspector init(
          String aggregateName, Map<Object,Object> expected, Set<Object> keys) {
        this.aggregateName = aggregateName;
        this.expected = expected;
        this.keys = keys;
        return this;
      }
    }.init(aggregateName, expected, keys));

    for (VectorizedRowBatch unit: data) {
      vgo.process(unit,  0);
    }
    vgo.close(false);

    List<Object> outBatchList = out.getCapturedRows();
    assertNotNull(outBatchList);
    assertEquals(expected.size(), outBatchList.size());
    assertEquals(expected.size(), keys.size());    
  }  


  public void testAggregateLongRepeats (
    String aggregateName,
    Long value,
    int repeat,
    int batchSize,
    Object expected) throws HiveException {
    FakeVectorRowBatchFromRepeats fdr = new FakeVectorRowBatchFromRepeats(
        new Long[] {value}, repeat, batchSize);
    testAggregateLongIterable (aggregateName, fdr, expected);
  }

  public HashMap<Object, Object> buildHashMap(Object... pairs) {
    HashMap<Object, Object> map = new HashMap<Object, Object>();
    for(int i = 0; i < pairs.length; i += 2) {
      map.put(pairs[i], pairs[i+1]);
    }
    return map;
  }

  public void testAggregateStringKeyAggregate (
      String aggregateName,
      int batchSize,
      Iterable<Object> list,
      Iterable<Object> values,
      HashMap<Object, Object> expected) throws HiveException {

    @SuppressWarnings("unchecked")
    FakeVectorRowBatchFromObjectIterables fdr = new FakeVectorRowBatchFromObjectIterables(
        batchSize,
        new String[] {"string", "long"},
        list,
        values);
    testAggregateStringKeyIterable (aggregateName, fdr,  TypeInfoFactory.longTypeInfo, expected);
  }

  public void testAggregateDoubleStringKeyAggregate (
      String aggregateName,
      int batchSize,
      Iterable<Object> list,
      Iterable<Object> values,
      HashMap<Object, Object> expected) throws HiveException {

    @SuppressWarnings("unchecked")
    FakeVectorRowBatchFromObjectIterables fdr = new FakeVectorRowBatchFromObjectIterables(
        batchSize,
        new String[] {"string", "double"},
        list,
        values);
    testAggregateStringKeyIterable (aggregateName, fdr,  TypeInfoFactory.doubleTypeInfo, expected);
  }

  public void testAggregateLongKeyAggregate (
      String aggregateName,
      int batchSize,
      List<Long> list,
      Iterable<Long> values,
      HashMap<Object, Object> expected) throws HiveException {

    @SuppressWarnings("unchecked")
    FakeVectorRowBatchFromLongIterables fdr = new FakeVectorRowBatchFromLongIterables(batchSize, list, values);
    testAggregateLongKeyIterable (aggregateName, fdr, expected);
  }

  public void testAggregateString (
      String aggregateName,
      int batchSize,
      Iterable<Object> values,
      Object expected) throws HiveException {

    @SuppressWarnings("unchecked")
    FakeVectorRowBatchFromObjectIterables fdr = new FakeVectorRowBatchFromObjectIterables(
        batchSize, new String[] {"string"}, values);
    testAggregateStringIterable (aggregateName, fdr, expected);
  }

  public void testAggregateDouble (
      String aggregateName,
      int batchSize,
      Iterable<Object> values,
      Object expected) throws HiveException {

    @SuppressWarnings("unchecked")
    FakeVectorRowBatchFromObjectIterables fdr = new FakeVectorRowBatchFromObjectIterables(
        batchSize, new String[] {"double"}, values);
    testAggregateDoubleIterable (aggregateName, fdr, expected);
  }


  public void testAggregateLongAggregate (
      String aggregateName,
      int batchSize,
      Iterable<Long> values,
      Object expected) throws HiveException {

    @SuppressWarnings("unchecked")
    FakeVectorRowBatchFromLongIterables fdr = new FakeVectorRowBatchFromLongIterables(batchSize, values);
    testAggregateLongIterable (aggregateName, fdr, expected);
  }

  public void testAggregateCountStar (
      int batchSize,
      Iterable<Long> values,
      Object expected) throws HiveException {

    @SuppressWarnings("unchecked")
    FakeVectorRowBatchFromLongIterables fdr = new FakeVectorRowBatchFromLongIterables(batchSize, values);
    testAggregateCountStarIterable (fdr, expected);
  }


  public static interface Validator {
    void validate (Object expected, Object result);
  };

  public static class ValueValidator implements Validator {
    @Override
    public void validate(Object expected, Object result) {

      assertEquals(true, result instanceof Object[]);
      Object[] arr = (Object[]) result;
      assertEquals(1, arr.length);

      if (expected == null) {
        assertNull (arr[0]);
      } else if (arr[0] instanceof LongWritable) {
        LongWritable lw = (LongWritable) arr[0];
        assertEquals((Long) expected, (Long) lw.get());
      } else if (arr[0] instanceof BytesWritable) {
        BytesWritable bw = (BytesWritable) arr[0];
        String sbw = new String(bw.getBytes());
        assertEquals((String) expected, sbw);
      } else if (arr[0] instanceof DoubleWritable) {
        DoubleWritable dw = (DoubleWritable) arr[0];
        assertEquals ((Double) expected, (Double) dw.get());
      } else if (arr[0] instanceof Double) {
        assertEquals ((Double) expected, (Double) arr[0]);
      } else if (arr[0] instanceof Long) {
        assertEquals ((Long) expected, (Long) arr[0]);
      } else {
        Assert.fail("Unsupported result type: " + arr[0].getClass().getName());
      }
    }
  }

  public static class AvgValidator implements Validator {

    @Override
    public void validate(Object expected, Object result) {
      Object[] arr = (Object[]) result;
      assertEquals (1, arr.length);

      if (expected == null) {
        assertNull (arr[0]);
      } else {
        assertEquals (true, arr[0] instanceof Object[]);
        Object[] vals = (Object[]) arr[0];
        assertEquals (2, vals.length);

        assertEquals (true, vals[0] instanceof LongWritable);
        assertEquals (true, vals[1] instanceof DoubleWritable);
        LongWritable lw = (LongWritable) vals[0];
        DoubleWritable dw = (DoubleWritable) vals[1];
        assertFalse (lw.get() == 0L);
        assertEquals ((Double) expected, (Double) (dw.get() / lw.get()));
      }
    }

  }

  public abstract static class BaseVarianceValidator implements Validator {

    abstract void validateVariance (
        double expected, long cnt, double sum, double variance);

    @Override
    public void validate(Object expected, Object result) {
      Object[] arr = (Object[]) result;
      assertEquals (1, arr.length);

      if (expected == null) {
        assertNull (arr[0]);
      } else {
        assertEquals (true, arr[0] instanceof Object[]);
        Object[] vals = (Object[]) arr[0];
        assertEquals (3, vals.length);

        assertEquals (true, vals[0] instanceof LongWritable);
        assertEquals (true, vals[1] instanceof DoubleWritable);
        assertEquals (true, vals[2] instanceof DoubleWritable);
        LongWritable cnt = (LongWritable) vals[0];
        DoubleWritable sum = (DoubleWritable) vals[1];
        DoubleWritable var = (DoubleWritable) vals[2];
        assertTrue (1 <= cnt.get());
        validateVariance ((Double) expected, cnt.get(), sum.get(), var.get());
      }
    }
  }

  public static class VarianceValidator extends BaseVarianceValidator {

    @Override
    void validateVariance(double expected, long cnt, double sum, double variance) {
      assertEquals (expected, variance /cnt, 0.0);
    }
  }

  public static class VarianceSampValidator extends BaseVarianceValidator {

    @Override
    void validateVariance(double expected, long cnt, double sum, double variance) {
      assertEquals (expected, variance /(cnt-1), 0.0);
    }
  }

  public static class StdValidator extends BaseVarianceValidator {

    @Override
    void validateVariance(double expected, long cnt, double sum, double variance) {
      assertEquals (expected, Math.sqrt(variance / cnt), 0.0);
    }
  }

  public static class StdSampValidator extends BaseVarianceValidator {

    @Override
    void validateVariance(double expected, long cnt, double sum, double variance) {
      assertEquals (expected, Math.sqrt(variance / (cnt-1)), 0.0);
    }
  }

  private static Object[][] validators = {
      {"count", ValueValidator.class},
      {"min", ValueValidator.class},
      {"max", ValueValidator.class},
      {"sum", ValueValidator.class},
      {"avg", AvgValidator.class},
      {"variance", VarianceValidator.class},
      {"var_pop", VarianceValidator.class},
      {"var_samp", VarianceSampValidator.class},
      {"std", StdValidator.class},
      {"stddev", StdValidator.class},
      {"stddev_samp", StdSampValidator.class},
  };

  public static Validator getValidator(String aggregate) throws HiveException {
    try
    {
      for (Object[] v: validators) {
        if (aggregate.equalsIgnoreCase((String) v[0])) {
          @SuppressWarnings("unchecked")
          Class<? extends Validator> c = (Class<? extends Validator>) v[1];
          Constructor<? extends Validator> ctr = c.getConstructor();
          return ctr.newInstance();
        }
      }
    }catch(Exception e) {
      throw new HiveException(e);
    }
    throw new HiveException("Missing validator for aggregate: " + aggregate);
  }

  public void testAggregateCountStarIterable (
      Iterable<VectorizedRowBatch> data,
      Object expected) throws HiveException {
    Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
    mapColumnNames.put("A", 0);
    VectorizationContext ctx = new VectorizationContext(mapColumnNames, 1);

    GroupByDesc desc = buildGroupByDescCountStar (ctx);

    VectorGroupByOperator vgo = new VectorGroupByOperator(ctx, desc);

    FakeCaptureOutputOperator out = FakeCaptureOutputOperator.addCaptureOutputChild(vgo);
    vgo.initialize(null, null);

    for (VectorizedRowBatch unit: data) {
      vgo.process(unit,  0);
    }
    vgo.close(false);

    List<Object> outBatchList = out.getCapturedRows();
    assertNotNull(outBatchList);
    assertEquals(1, outBatchList.size());

    Object result = outBatchList.get(0);

    Validator validator = getValidator("count");
    validator.validate(expected, result);
  }

  public void testAggregateStringIterable (
      String aggregateName,
      Iterable<VectorizedRowBatch> data,
      Object expected) throws HiveException {
    Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
    mapColumnNames.put("A", 0);
    VectorizationContext ctx = new VectorizationContext(mapColumnNames, 1);

    GroupByDesc desc = buildGroupByDescType (ctx, aggregateName, "A", TypeInfoFactory.stringTypeInfo);

    VectorGroupByOperator vgo = new VectorGroupByOperator(ctx, desc);

    FakeCaptureOutputOperator out = FakeCaptureOutputOperator.addCaptureOutputChild(vgo);
    vgo.initialize(null, null);

    for (VectorizedRowBatch unit: data) {
      vgo.process(unit,  0);
    }
    vgo.close(false);

    List<Object> outBatchList = out.getCapturedRows();
    assertNotNull(outBatchList);
    assertEquals(1, outBatchList.size());

    Object result = outBatchList.get(0);

    Validator validator = getValidator(aggregateName);
    validator.validate(expected, result);
  }

  public void testAggregateDoubleIterable (
      String aggregateName,
      Iterable<VectorizedRowBatch> data,
      Object expected) throws HiveException {
    Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
    mapColumnNames.put("A", 0);
    VectorizationContext ctx = new VectorizationContext(mapColumnNames, 1);

    GroupByDesc desc = buildGroupByDescType (ctx, aggregateName, "A", TypeInfoFactory.doubleTypeInfo);

    VectorGroupByOperator vgo = new VectorGroupByOperator(ctx, desc);

    FakeCaptureOutputOperator out = FakeCaptureOutputOperator.addCaptureOutputChild(vgo);
    vgo.initialize(null, null);

    for (VectorizedRowBatch unit: data) {
      vgo.process(unit,  0);
    }
    vgo.close(false);

    List<Object> outBatchList = out.getCapturedRows();
    assertNotNull(outBatchList);
    assertEquals(1, outBatchList.size());

    Object result = outBatchList.get(0);

    Validator validator = getValidator(aggregateName);
    validator.validate(expected, result);
  }

  public void testAggregateLongIterable (
      String aggregateName,
      Iterable<VectorizedRowBatch> data,
      Object expected) throws HiveException {
    Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
    mapColumnNames.put("A", 0);
    VectorizationContext ctx = new VectorizationContext(mapColumnNames, 1);

    GroupByDesc desc = buildGroupByDescType(ctx, aggregateName, "A", TypeInfoFactory.longTypeInfo);

    VectorGroupByOperator vgo = new VectorGroupByOperator(ctx, desc);

    FakeCaptureOutputOperator out = FakeCaptureOutputOperator.addCaptureOutputChild(vgo);
    vgo.initialize(null, null);

    for (VectorizedRowBatch unit: data) {
      vgo.process(unit,  0);
    }
    vgo.close(false);

    List<Object> outBatchList = out.getCapturedRows();
    assertNotNull(outBatchList);
    assertEquals(1, outBatchList.size());

    Object result = outBatchList.get(0);

    Validator validator = getValidator(aggregateName);
    validator.validate(expected, result);
  }

  public void testAggregateLongKeyIterable (
      String aggregateName,
      Iterable<VectorizedRowBatch> data,
      HashMap<Object,Object> expected) throws HiveException {
    Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
    mapColumnNames.put("Key", 0);
    mapColumnNames.put("Value", 1);
    VectorizationContext ctx = new VectorizationContext(mapColumnNames, 2);
    Set<Object> keys = new HashSet<Object>();

    GroupByDesc desc = buildKeyGroupByDesc (ctx, aggregateName, "Value",
        TypeInfoFactory.longTypeInfo, "Key", TypeInfoFactory.longTypeInfo);

    VectorGroupByOperator vgo = new VectorGroupByOperator(ctx, desc);

    FakeCaptureOutputOperator out = FakeCaptureOutputOperator.addCaptureOutputChild(vgo);
    vgo.initialize(null, null);
    out.setOutputInspector(new FakeCaptureOutputOperator.OutputInspector() {

      private int rowIndex;
      private String aggregateName;
      private HashMap<Object,Object> expected;
      private Set<Object> keys;

      @Override
      public void inspectRow(Object row, int tag) throws HiveException {
        assertTrue(row instanceof Object[]);
        Object[] fields = (Object[]) row;
        assertEquals(2, fields.length);
        Object key = fields[0];
        Long keyValue = null;
        if (null != key) {
          assertTrue(key instanceof LongWritable);
          LongWritable lwKey = (LongWritable)key;
          keyValue = lwKey.get();
        }
        assertTrue(expected.containsKey(keyValue));
        Object expectedValue = expected.get(keyValue);
        Object value = fields[1];
        Validator validator = getValidator(aggregateName);
        validator.validate(expectedValue, new Object[] {value});
        keys.add(keyValue);
      }

      private FakeCaptureOutputOperator.OutputInspector init(
          String aggregateName, HashMap<Object,Object> expected, Set<Object> keys) {
        this.aggregateName = aggregateName;
        this.expected = expected;
        this.keys = keys;
        return this;
      }
    }.init(aggregateName, expected, keys));

    for (VectorizedRowBatch unit: data) {
      vgo.process(unit,  0);
    }
    vgo.close(false);

    List<Object> outBatchList = out.getCapturedRows();
    assertNotNull(outBatchList);
    assertEquals(expected.size(), outBatchList.size());
    assertEquals(expected.size(), keys.size());
  }

  public void testAggregateStringKeyIterable (
      String aggregateName,
      Iterable<VectorizedRowBatch> data,
      TypeInfo dataTypeInfo,
      HashMap<Object,Object> expected) throws HiveException {
    Map<String, Integer> mapColumnNames = new HashMap<String, Integer>();
    mapColumnNames.put("Key", 0);
    mapColumnNames.put("Value", 1);
    VectorizationContext ctx = new VectorizationContext(mapColumnNames, 2);
    Set<Object> keys = new HashSet<Object>();

    GroupByDesc desc = buildKeyGroupByDesc (ctx, aggregateName, "Value",
       dataTypeInfo, "Key", TypeInfoFactory.stringTypeInfo);

    VectorGroupByOperator vgo = new VectorGroupByOperator(ctx, desc);

    FakeCaptureOutputOperator out = FakeCaptureOutputOperator.addCaptureOutputChild(vgo);
    vgo.initialize(null, null);
    out.setOutputInspector(new FakeCaptureOutputOperator.OutputInspector() {

      private int rowIndex;
      private String aggregateName;
      private HashMap<Object,Object> expected;
      private Set<Object> keys;

      @SuppressWarnings("deprecation")
      @Override
      public void inspectRow(Object row, int tag) throws HiveException {
        assertTrue(row instanceof Object[]);
        Object[] fields = (Object[]) row;
        assertEquals(2, fields.length);
        Object key = fields[0];
        String keyValue = null;
        if (null != key) {
          assertTrue(key instanceof BytesWritable);
          BytesWritable bwKey = (BytesWritable)key;
          keyValue = new String(bwKey.get());
        }
        assertTrue(expected.containsKey(keyValue));
        Object expectedValue = expected.get(keyValue);
        Object value = fields[1];
        Validator validator = getValidator(aggregateName);
        validator.validate(expectedValue, new Object[] {value});
        keys.add(keyValue);
      }

      private FakeCaptureOutputOperator.OutputInspector init(
          String aggregateName, HashMap<Object,Object> expected, Set<Object> keys) {
        this.aggregateName = aggregateName;
        this.expected = expected;
        this.keys = keys;
        return this;
      }
    }.init(aggregateName, expected, keys));

    for (VectorizedRowBatch unit: data) {
      vgo.process(unit,  0);
    }
    vgo.close(false);

    List<Object> outBatchList = out.getCapturedRows();
    assertNotNull(outBatchList);
    assertEquals(expected.size(), outBatchList.size());
    assertEquals(expected.size(), keys.size());
  }


}

