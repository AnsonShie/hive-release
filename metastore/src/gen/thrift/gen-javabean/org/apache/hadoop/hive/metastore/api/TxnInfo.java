/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-05-25")
public class TxnInfo implements org.apache.thrift.TBase<TxnInfo, TxnInfo._Fields>, java.io.Serializable, Cloneable, Comparable<TxnInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TxnInfo");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField STATE_FIELD_DESC = new org.apache.thrift.protocol.TField("state", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField USER_FIELD_DESC = new org.apache.thrift.protocol.TField("user", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField HOSTNAME_FIELD_DESC = new org.apache.thrift.protocol.TField("hostname", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField AGENT_INFO_FIELD_DESC = new org.apache.thrift.protocol.TField("agentInfo", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField HEARTBEAT_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("heartbeatCount", org.apache.thrift.protocol.TType.I32, (short)6);
  private static final org.apache.thrift.protocol.TField META_INFO_FIELD_DESC = new org.apache.thrift.protocol.TField("metaInfo", org.apache.thrift.protocol.TType.STRING, (short)7);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TxnInfoStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TxnInfoTupleSchemeFactory());
  }

  private long id; // required
  private TxnState state; // required
  private String user; // required
  private String hostname; // required
  private String agentInfo; // optional
  private int heartbeatCount; // optional
  private String metaInfo; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    /**
     * 
     * @see TxnState
     */
    STATE((short)2, "state"),
    USER((short)3, "user"),
    HOSTNAME((short)4, "hostname"),
    AGENT_INFO((short)5, "agentInfo"),
    HEARTBEAT_COUNT((short)6, "heartbeatCount"),
    META_INFO((short)7, "metaInfo");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID
          return ID;
        case 2: // STATE
          return STATE;
        case 3: // USER
          return USER;
        case 4: // HOSTNAME
          return HOSTNAME;
        case 5: // AGENT_INFO
          return AGENT_INFO;
        case 6: // HEARTBEAT_COUNT
          return HEARTBEAT_COUNT;
        case 7: // META_INFO
          return META_INFO;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ID_ISSET_ID = 0;
  private static final int __HEARTBEATCOUNT_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.AGENT_INFO,_Fields.HEARTBEAT_COUNT,_Fields.META_INFO};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.STATE, new org.apache.thrift.meta_data.FieldMetaData("state", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, TxnState.class)));
    tmpMap.put(_Fields.USER, new org.apache.thrift.meta_data.FieldMetaData("user", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.HOSTNAME, new org.apache.thrift.meta_data.FieldMetaData("hostname", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.AGENT_INFO, new org.apache.thrift.meta_data.FieldMetaData("agentInfo", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.HEARTBEAT_COUNT, new org.apache.thrift.meta_data.FieldMetaData("heartbeatCount", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.META_INFO, new org.apache.thrift.meta_data.FieldMetaData("metaInfo", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TxnInfo.class, metaDataMap);
  }

  public TxnInfo() {
    this.agentInfo = "Unknown";

    this.heartbeatCount = 0;

  }

  public TxnInfo(
    long id,
    TxnState state,
    String user,
    String hostname)
  {
    this();
    this.id = id;
    setIdIsSet(true);
    this.state = state;
    this.user = user;
    this.hostname = hostname;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TxnInfo(TxnInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id = other.id;
    if (other.isSetState()) {
      this.state = other.state;
    }
    if (other.isSetUser()) {
      this.user = other.user;
    }
    if (other.isSetHostname()) {
      this.hostname = other.hostname;
    }
    if (other.isSetAgentInfo()) {
      this.agentInfo = other.agentInfo;
    }
    this.heartbeatCount = other.heartbeatCount;
    if (other.isSetMetaInfo()) {
      this.metaInfo = other.metaInfo;
    }
  }

  public TxnInfo deepCopy() {
    return new TxnInfo(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.id = 0;
    this.state = null;
    this.user = null;
    this.hostname = null;
    this.agentInfo = "Unknown";

    this.heartbeatCount = 0;

    this.metaInfo = null;
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
    setIdIsSet(true);
  }

  public void unsetId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID_ISSET_ID);
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return EncodingUtils.testBit(__isset_bitfield, __ID_ISSET_ID);
  }

  public void setIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID_ISSET_ID, value);
  }

  /**
   * 
   * @see TxnState
   */
  public TxnState getState() {
    return this.state;
  }

  /**
   * 
   * @see TxnState
   */
  public void setState(TxnState state) {
    this.state = state;
  }

  public void unsetState() {
    this.state = null;
  }

  /** Returns true if field state is set (has been assigned a value) and false otherwise */
  public boolean isSetState() {
    return this.state != null;
  }

  public void setStateIsSet(boolean value) {
    if (!value) {
      this.state = null;
    }
  }

  public String getUser() {
    return this.user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void unsetUser() {
    this.user = null;
  }

  /** Returns true if field user is set (has been assigned a value) and false otherwise */
  public boolean isSetUser() {
    return this.user != null;
  }

  public void setUserIsSet(boolean value) {
    if (!value) {
      this.user = null;
    }
  }

  public String getHostname() {
    return this.hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public void unsetHostname() {
    this.hostname = null;
  }

  /** Returns true if field hostname is set (has been assigned a value) and false otherwise */
  public boolean isSetHostname() {
    return this.hostname != null;
  }

  public void setHostnameIsSet(boolean value) {
    if (!value) {
      this.hostname = null;
    }
  }

  public String getAgentInfo() {
    return this.agentInfo;
  }

  public void setAgentInfo(String agentInfo) {
    this.agentInfo = agentInfo;
  }

  public void unsetAgentInfo() {
    this.agentInfo = null;
  }

  /** Returns true if field agentInfo is set (has been assigned a value) and false otherwise */
  public boolean isSetAgentInfo() {
    return this.agentInfo != null;
  }

  public void setAgentInfoIsSet(boolean value) {
    if (!value) {
      this.agentInfo = null;
    }
  }

  public int getHeartbeatCount() {
    return this.heartbeatCount;
  }

  public void setHeartbeatCount(int heartbeatCount) {
    this.heartbeatCount = heartbeatCount;
    setHeartbeatCountIsSet(true);
  }

  public void unsetHeartbeatCount() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __HEARTBEATCOUNT_ISSET_ID);
  }

  /** Returns true if field heartbeatCount is set (has been assigned a value) and false otherwise */
  public boolean isSetHeartbeatCount() {
    return EncodingUtils.testBit(__isset_bitfield, __HEARTBEATCOUNT_ISSET_ID);
  }

  public void setHeartbeatCountIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __HEARTBEATCOUNT_ISSET_ID, value);
  }

  public String getMetaInfo() {
    return this.metaInfo;
  }

  public void setMetaInfo(String metaInfo) {
    this.metaInfo = metaInfo;
  }

  public void unsetMetaInfo() {
    this.metaInfo = null;
  }

  /** Returns true if field metaInfo is set (has been assigned a value) and false otherwise */
  public boolean isSetMetaInfo() {
    return this.metaInfo != null;
  }

  public void setMetaInfoIsSet(boolean value) {
    if (!value) {
      this.metaInfo = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((Long)value);
      }
      break;

    case STATE:
      if (value == null) {
        unsetState();
      } else {
        setState((TxnState)value);
      }
      break;

    case USER:
      if (value == null) {
        unsetUser();
      } else {
        setUser((String)value);
      }
      break;

    case HOSTNAME:
      if (value == null) {
        unsetHostname();
      } else {
        setHostname((String)value);
      }
      break;

    case AGENT_INFO:
      if (value == null) {
        unsetAgentInfo();
      } else {
        setAgentInfo((String)value);
      }
      break;

    case HEARTBEAT_COUNT:
      if (value == null) {
        unsetHeartbeatCount();
      } else {
        setHeartbeatCount((Integer)value);
      }
      break;

    case META_INFO:
      if (value == null) {
        unsetMetaInfo();
      } else {
        setMetaInfo((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return getId();

    case STATE:
      return getState();

    case USER:
      return getUser();

    case HOSTNAME:
      return getHostname();

    case AGENT_INFO:
      return getAgentInfo();

    case HEARTBEAT_COUNT:
      return getHeartbeatCount();

    case META_INFO:
      return getMetaInfo();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case STATE:
      return isSetState();
    case USER:
      return isSetUser();
    case HOSTNAME:
      return isSetHostname();
    case AGENT_INFO:
      return isSetAgentInfo();
    case HEARTBEAT_COUNT:
      return isSetHeartbeatCount();
    case META_INFO:
      return isSetMetaInfo();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TxnInfo)
      return this.equals((TxnInfo)that);
    return false;
  }

  public boolean equals(TxnInfo that) {
    if (that == null)
      return false;

    boolean this_present_id = true;
    boolean that_present_id = true;
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (this.id != that.id)
        return false;
    }

    boolean this_present_state = true && this.isSetState();
    boolean that_present_state = true && that.isSetState();
    if (this_present_state || that_present_state) {
      if (!(this_present_state && that_present_state))
        return false;
      if (!this.state.equals(that.state))
        return false;
    }

    boolean this_present_user = true && this.isSetUser();
    boolean that_present_user = true && that.isSetUser();
    if (this_present_user || that_present_user) {
      if (!(this_present_user && that_present_user))
        return false;
      if (!this.user.equals(that.user))
        return false;
    }

    boolean this_present_hostname = true && this.isSetHostname();
    boolean that_present_hostname = true && that.isSetHostname();
    if (this_present_hostname || that_present_hostname) {
      if (!(this_present_hostname && that_present_hostname))
        return false;
      if (!this.hostname.equals(that.hostname))
        return false;
    }

    boolean this_present_agentInfo = true && this.isSetAgentInfo();
    boolean that_present_agentInfo = true && that.isSetAgentInfo();
    if (this_present_agentInfo || that_present_agentInfo) {
      if (!(this_present_agentInfo && that_present_agentInfo))
        return false;
      if (!this.agentInfo.equals(that.agentInfo))
        return false;
    }

    boolean this_present_heartbeatCount = true && this.isSetHeartbeatCount();
    boolean that_present_heartbeatCount = true && that.isSetHeartbeatCount();
    if (this_present_heartbeatCount || that_present_heartbeatCount) {
      if (!(this_present_heartbeatCount && that_present_heartbeatCount))
        return false;
      if (this.heartbeatCount != that.heartbeatCount)
        return false;
    }

    boolean this_present_metaInfo = true && this.isSetMetaInfo();
    boolean that_present_metaInfo = true && that.isSetMetaInfo();
    if (this_present_metaInfo || that_present_metaInfo) {
      if (!(this_present_metaInfo && that_present_metaInfo))
        return false;
      if (!this.metaInfo.equals(that.metaInfo))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_id = true;
    list.add(present_id);
    if (present_id)
      list.add(id);

    boolean present_state = true && (isSetState());
    list.add(present_state);
    if (present_state)
      list.add(state.getValue());

    boolean present_user = true && (isSetUser());
    list.add(present_user);
    if (present_user)
      list.add(user);

    boolean present_hostname = true && (isSetHostname());
    list.add(present_hostname);
    if (present_hostname)
      list.add(hostname);

    boolean present_agentInfo = true && (isSetAgentInfo());
    list.add(present_agentInfo);
    if (present_agentInfo)
      list.add(agentInfo);

    boolean present_heartbeatCount = true && (isSetHeartbeatCount());
    list.add(present_heartbeatCount);
    if (present_heartbeatCount)
      list.add(heartbeatCount);

    boolean present_metaInfo = true && (isSetMetaInfo());
    list.add(present_metaInfo);
    if (present_metaInfo)
      list.add(metaInfo);

    return list.hashCode();
  }

  @Override
  public int compareTo(TxnInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(other.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, other.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetState()).compareTo(other.isSetState());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetState()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.state, other.state);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUser()).compareTo(other.isSetUser());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUser()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.user, other.user);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetHostname()).compareTo(other.isSetHostname());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHostname()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hostname, other.hostname);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAgentInfo()).compareTo(other.isSetAgentInfo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAgentInfo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.agentInfo, other.agentInfo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetHeartbeatCount()).compareTo(other.isSetHeartbeatCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHeartbeatCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.heartbeatCount, other.heartbeatCount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMetaInfo()).compareTo(other.isSetMetaInfo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMetaInfo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.metaInfo, other.metaInfo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TxnInfo(");
    boolean first = true;

    sb.append("id:");
    sb.append(this.id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("state:");
    if (this.state == null) {
      sb.append("null");
    } else {
      sb.append(this.state);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("user:");
    if (this.user == null) {
      sb.append("null");
    } else {
      sb.append(this.user);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("hostname:");
    if (this.hostname == null) {
      sb.append("null");
    } else {
      sb.append(this.hostname);
    }
    first = false;
    if (isSetAgentInfo()) {
      if (!first) sb.append(", ");
      sb.append("agentInfo:");
      if (this.agentInfo == null) {
        sb.append("null");
      } else {
        sb.append(this.agentInfo);
      }
      first = false;
    }
    if (isSetHeartbeatCount()) {
      if (!first) sb.append(", ");
      sb.append("heartbeatCount:");
      sb.append(this.heartbeatCount);
      first = false;
    }
    if (isSetMetaInfo()) {
      if (!first) sb.append(", ");
      sb.append("metaInfo:");
      if (this.metaInfo == null) {
        sb.append("null");
      } else {
        sb.append(this.metaInfo);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'id' is unset! Struct:" + toString());
    }

    if (!isSetState()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'state' is unset! Struct:" + toString());
    }

    if (!isSetUser()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'user' is unset! Struct:" + toString());
    }

    if (!isSetHostname()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'hostname' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TxnInfoStandardSchemeFactory implements SchemeFactory {
    public TxnInfoStandardScheme getScheme() {
      return new TxnInfoStandardScheme();
    }
  }

  private static class TxnInfoStandardScheme extends StandardScheme<TxnInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TxnInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.id = iprot.readI64();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STATE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.state = org.apache.hadoop.hive.metastore.api.TxnState.findByValue(iprot.readI32());
              struct.setStateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // USER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.user = iprot.readString();
              struct.setUserIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // HOSTNAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.hostname = iprot.readString();
              struct.setHostnameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // AGENT_INFO
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.agentInfo = iprot.readString();
              struct.setAgentInfoIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // HEARTBEAT_COUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.heartbeatCount = iprot.readI32();
              struct.setHeartbeatCountIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // META_INFO
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.metaInfo = iprot.readString();
              struct.setMetaInfoIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TxnInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI64(struct.id);
      oprot.writeFieldEnd();
      if (struct.state != null) {
        oprot.writeFieldBegin(STATE_FIELD_DESC);
        oprot.writeI32(struct.state.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.user != null) {
        oprot.writeFieldBegin(USER_FIELD_DESC);
        oprot.writeString(struct.user);
        oprot.writeFieldEnd();
      }
      if (struct.hostname != null) {
        oprot.writeFieldBegin(HOSTNAME_FIELD_DESC);
        oprot.writeString(struct.hostname);
        oprot.writeFieldEnd();
      }
      if (struct.agentInfo != null) {
        if (struct.isSetAgentInfo()) {
          oprot.writeFieldBegin(AGENT_INFO_FIELD_DESC);
          oprot.writeString(struct.agentInfo);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetHeartbeatCount()) {
        oprot.writeFieldBegin(HEARTBEAT_COUNT_FIELD_DESC);
        oprot.writeI32(struct.heartbeatCount);
        oprot.writeFieldEnd();
      }
      if (struct.metaInfo != null) {
        if (struct.isSetMetaInfo()) {
          oprot.writeFieldBegin(META_INFO_FIELD_DESC);
          oprot.writeString(struct.metaInfo);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TxnInfoTupleSchemeFactory implements SchemeFactory {
    public TxnInfoTupleScheme getScheme() {
      return new TxnInfoTupleScheme();
    }
  }

  private static class TxnInfoTupleScheme extends TupleScheme<TxnInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TxnInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.id);
      oprot.writeI32(struct.state.getValue());
      oprot.writeString(struct.user);
      oprot.writeString(struct.hostname);
      BitSet optionals = new BitSet();
      if (struct.isSetAgentInfo()) {
        optionals.set(0);
      }
      if (struct.isSetHeartbeatCount()) {
        optionals.set(1);
      }
      if (struct.isSetMetaInfo()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetAgentInfo()) {
        oprot.writeString(struct.agentInfo);
      }
      if (struct.isSetHeartbeatCount()) {
        oprot.writeI32(struct.heartbeatCount);
      }
      if (struct.isSetMetaInfo()) {
        oprot.writeString(struct.metaInfo);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TxnInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.id = iprot.readI64();
      struct.setIdIsSet(true);
      struct.state = org.apache.hadoop.hive.metastore.api.TxnState.findByValue(iprot.readI32());
      struct.setStateIsSet(true);
      struct.user = iprot.readString();
      struct.setUserIsSet(true);
      struct.hostname = iprot.readString();
      struct.setHostnameIsSet(true);
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.agentInfo = iprot.readString();
        struct.setAgentInfoIsSet(true);
      }
      if (incoming.get(1)) {
        struct.heartbeatCount = iprot.readI32();
        struct.setHeartbeatCountIsSet(true);
      }
      if (incoming.get(2)) {
        struct.metaInfo = iprot.readString();
        struct.setMetaInfoIsSet(true);
      }
    }
  }

}

