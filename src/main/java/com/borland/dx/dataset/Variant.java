//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.Diagnostic;

/**
 * The Variant class is a type of storage class whose value can be one of many
 * data types. It can hold data of these types:
 * <p>
 * <ul>
 * <li>byte</li>
 * <li>short</li>
 * <li>int</li>
 * <li>long</li>
 * <li>float</li>
 * <li>double</li>
 * <li>BigDecimal</li>
 * <li>boolean</li>
 * <li>input</li>
 * <li>stream</li>
 * <li>Date</li>
 * <li>Time</li>
 * <li>Timestamp</li>
 * <li>String</li>
 * <li>Object</li>
 * <li>byte</li>
 * <li>array</li>
 * </ul>
 * <p>
 * Variant contains constants used to identify all of these data types. It also
 * contains the methods to get and set data values and to perform operations on
 * Variant data, such as addition, subtraction, and comparing one value to
 * another. The dataset package uses the Variant data type frequently because it
 * can handle all types of data.
 *
 */
public class Variant implements Cloneable, Serializable {
	private static final long serialVersionUID = 200L;

	// UNASSIGNED_NULL - ASSIGNED_NULL expected to belong to a contiguous range
	//

	/**
	 * An integer constant used to identify an unassigned null value. An
	 * unassigned null value is a data value that was never assigned. This is in
	 * contrast to an assigned null value that is explicitly assigned.
	 *
	 * @see #ASSIGNED_NULL
	 */
	public static final int UNASSIGNED_NULL = 0;

	/**
	 * Constant that identifies a data type for values that are explicitly set to
	 * null. This is in contrast to data that is never assigned.
	 *
	 * @see #UNASSIGNED_NULL
	 */
	public static final int ASSIGNED_NULL = 1;

	/**
	 * An integer constant used to identify null data. Null data can be either
	 * assigned or unassigned.
	 *
	 * @see #ASSIGNED_NULL
	 * @see #UNASSIGNED_NULL
	 */
	public static final int NULL_TYPES = 1;

	// BYTE - LONG expected to belong to a contiguous range
	//
	/**
	 * An integer constant used to identify data of type <b>byte</b>.
	 */
	public static final int BYTE = 2;

	/**
	 * An integer constant used to identify the <b>short</b> data type.
	 */
	public static final int SHORT = 3;

	/**
	 * An integer constant used to identify the <b>int</b> data type.
	 */
	public static final int INT = 4;

	/**
	 * An integer constant used to identify the <b>long</b> data type.
	 */
	public static final int LONG = 5;

	// FLOAT - DOUBLE expected to belong to a contiguous range
	//
	/**
	 * An integer constant used to identify the <b>float</b> data type.
	 */
	public static final int FLOAT = 6;

	/**
	 * An integer constant used to identify the <b>double</b> data type.
	 */
	public static final int DOUBLE = 7;

	/**
	 * An integer constant used to identify the BigDecimal data type. BigDecimal
	 * values have an unlimited precision integer value and an integer scale
	 * factor.
	 */
	public static final int BIGDECIMAL = 10;

	/**
	 * An integer constant used to identify data of type <b>boolean</b>.
	 */
	public static final int BOOLEAN = 11;

	/**
	 * @deprecated use INPUTSTREAM.
	 */
	@Deprecated
	public static final int BINARY_STREAM = 12;

	/**
	 * An integer constant used to identify data of a input stream.
	 */
	public static final int INPUTSTREAM = 12;

	/**
	 * An integer constant used to identify the Date data type.
	 */
	public static final int DATE = 13;

	/**
	 * An integer constant used to identify the Time data type.
	 */
	public static final int TIME = 14;

	/**
	 * An integer constant used to identify the TimeStamp data type.
	 */
	public static final int TIMESTAMP = 15;

	/**
	 * An integer constant used to identify the String data type.
	 */
	public static final int STRING = 16;

	/**
	 * An integer constant used to identify the Object data type.
	 */
	public static final int OBJECT = 17;

	/**
	 * An integer constant used to identify data in a <b>byte</b> array.
	 */
	public static final int BYTE_ARRAY = 18;

	/**
	 * Type names
	 */

	/**
	 * A constant that displays an assigned <b>null</b> value as the string
	 * "ASSIGNED_NULL". An assigned null is a value explicitly set to <b>null</b>
	 * in contrast to one that is simply not assigned.
	 */
	public static final String AssignedNull_S = "ASSIGNED_NULL"; // NORES

	/**
	 * A constant that represents an unassigned null as the string
	 * "UNASSIGNED_NULL".
	 */
	public static final String UnassignedNull_S = "UNASSIGNED_NULL"; // NORES

	/**
	 * A constant that represents the <b>byte</b> data type as the string "BYTE".
	 */
	public static final String ByteType_S = "BYTE"; // NORES

	/**
	 * A constant that represents the <b>short</b> data type as the string
	 * "SHORT".
	 */
	public static final String ShortType_S = "SHORT"; // NORES

	/**
	 * A constant that represents the <b>int</b> data type as the string "INT".
	 */
	public static final String IntType_S = "INT"; // NORES

	/**
	 * A constant that represents the <b>long</b> data type as the string "LONG".
	 */
	public static final String LongType_S = "LONG"; // NORES

	/**
	 * A constant that represents the <b>float</b> data type as the string
	 * "FLOAT".
	 */
	public static final String FloatType_S = "FLOAT"; // NORES

	/**
	 * A constant that represents the <b>double</b> date type as the string
	 * "DOUBLE".
	 */
	public static final String DoubleType_S = "DOUBLE"; // NORES

	/**
	 * A constant that represents the BigDecimal data type as the string
	 * "BIGDECIMAL".
	 */
	public static final String BigDecimalType_S = "BIGDECIMAL"; // NORES

	/**
	 * A constant that represents the <b>boolean</b> data type as the string
	 * "BOOLEAN".
	 */
	public static final String BooleanType_S = "BOOLEAN"; // NORES

	/**
	 * A constant that represents the INPUTSTREAM data type as the string
	 * "INPUTSTREAM".
	 */
	public static final String InputStreamType_S = "INPUTSTREAM"; // NORES

	/**
	 * @deprecated Use {@link #InputStreamType_S} instead.
	 */
	@Deprecated
	public static final String BinaryStreamType_S = "BINARY_STREAM"; // NORES

	/**
	 * A constant that represents the Date data type as the string "DATE".
	 */
	public static final String DateType_S = "DATE"; // NORES

	/**
	 * A constant that represents the Time data type as the string "TIME".
	 */
	public static final String TimeType_S = "TIME"; // NORES

	/**
	 * A constant that represents the TimeStamp data type as the string
	 * "TIMESTAMP".
	 */
	public static final String TimestampType_S = "TIMESTAMP"; // NORES

	/**
	 * A constant that displays a <b>byte</b> array as the string "BYTE_ARRAY".
	 */
	public static final String ByteArrayType_S = "BYTE_ARRAY"; // NORES

	/**
	 * A constant that represents the String data type as the string "STRING".
	 */
	public static final String StringType_S = "STRING"; // NORES

	/**
	 * A constant that represents the Object data type as the string "OBJECT".
	 */
	public static final String ObjectType_S = "OBJECT"; // NORES

	/**
	 * A constant that represents an unknown data type as the string "UNKNOWN".
	 */
	public static final String UnknownType_S = "UNKNOWN"; // NORES

	/**
	 * An integer constant used to identify a Variant data type with an unassigned
	 * null data value.
	 */
	public static final Variant nullVariant = new Variant(UNASSIGNED_NULL);

	/*
	 * private static final int SET_AS_INPUTSTREAM = -2; private static final int
	 * SET_AS_OBJECT = -3; private static final int SET_AS_LONG = -4; private
	 * static final int SET_AS_DATE = -5; private static final int SET_AS_TIME =
	 * -6; private static final int SET_AS_BOTH = -7;
	 */

	// ! NOTE! NOTE! NOTE!
	// ! If you add anything here or change the order, be sure to update the
	// String[]
	// ! for the DataTypeEditor in jbcl\editors! It must be perfectly parallel
	// ! to the order and size of these types.
	/**
	 * The maximum number of data types Variant can handle.
	 */
	public static final int MaxTypes = 18;

	// !RC TODO <ron> Made this public to know range of legal data types

	/**
	 * Constructs a Variant object that can contain data of the type specified
	 * with the dataType parameter. Variants instantiated with this constructor
	 * must have the results of all get or set operations be of the type
	 * specified.
	 *
	 * @param dataType
	 *          If this constructor is used, all set operations must be of the
	 *          data type from which the Variant was constructed.
	 */
	public Variant(int dataType) {
		setType = dataType;
	}

	/**
	 * Constructs a Variant object without specifying the explicit data type.
	 */
	public Variant() {
	}

	/**
	 * Returns the name of a data type as a string. For example, the string
	 * representation of a BOOLEAN data type is "BOOLEAN".
	 *
	 * @param type
	 *          The data type. Specify the type using one of the data type
	 *          constants of Variant. For example, BOOLEAN is the name of the
	 *          constant for a boolean data type.
	 * @return The name of a data type as a string.
	 */
	public static String typeName(int type) {
		switch (type) {
		case ASSIGNED_NULL:
			return AssignedNull_S;
		case UNASSIGNED_NULL:
			return UnassignedNull_S;

		case BYTE:
			return ByteType_S;
		case SHORT:
			return ShortType_S;
		case INT:
			return IntType_S;
		case LONG:
			return LongType_S;

		case FLOAT:
			return FloatType_S;
		case DOUBLE:
			return DoubleType_S;

		case BIGDECIMAL:
			return BigDecimalType_S;

		case BOOLEAN:
			return BooleanType_S;

		case INPUTSTREAM:
			return InputStreamType_S;

		case DATE:
			return DateType_S;
		case TIME:
			return TimeType_S;
		case TIMESTAMP:
			return TimestampType_S;

		case STRING:
			return StringType_S;
		case BYTE_ARRAY:
			return ByteArrayType_S;
		case OBJECT:
			return ObjectType_S;
		default:
			return UnknownType_S;
		}
	}

	/**
	 * Returns an integer that identifies the data type specified in the typeName
	 * parameter.
	 *
	 * @param typeName
	 *          The name of a data type as a string. For example, the string
	 *          "BOOLEAN" results in an integer value of 11, which is the value of
	 *          the BOOLEAN constant.
	 * @return An integer that identifies the data type specified in the typeName
	 *         parameter.
	 */
	public static int typeOf(String typeName) {
		// Put more common ones first.
		//
		if (typeName.equals(StringType_S))
			return STRING;
		if (typeName.equals(DateType_S))
			return DATE;
		if (typeName.equals(TimeType_S))
			return TIME;
		if (typeName.equals(TimestampType_S))
			return TIMESTAMP;
		if (typeName.equals(IntType_S))
			return INT;
		if (typeName.equals(BigDecimalType_S))
			return BIGDECIMAL;

		if (typeName.equals(AssignedNull_S))
			return ASSIGNED_NULL;
		if (typeName.equals(UnassignedNull_S))
			return UNASSIGNED_NULL;

		if (typeName.equals(ByteType_S))
			return BYTE;
		if (typeName.equals(ShortType_S))
			return SHORT;
		if (typeName.equals(LongType_S))
			return LONG;

		if (typeName.equals(DoubleType_S))
			return DOUBLE;
		if (typeName.equals(FloatType_S))
			return FLOAT;

		if (typeName.equals(BooleanType_S))
			return BOOLEAN;

		if (typeName.equals(BinaryStreamType_S))
			return INPUTSTREAM;
		if (typeName.equals(InputStreamType_S))
			return INPUTSTREAM;
		if (typeName.equals(ByteArrayType_S))
			return BYTE_ARRAY;

		if (typeName.equals(ObjectType_S))
			return OBJECT;

		VariantException.fire(Res.bundle.format(ResIndex.InvalidVariantName,
				new String[] { typeName }));
		return 0;
	}

	/**
	 * Returns an integer that identifies the data type specified in the typeName
	 * parameter.
	 *
	 * @param typeName
	 *          The name of a data type as a string. For example, the string
	 *          "BOOLEAN" results in an integer value of 11, which is the value of
	 *          the BOOLEAN constant.
	 * @return An integer that identifies the data type specified in the typeName
	 *         parameter.
	 */
	public static int typeOf(Object o) {
		// Put more common ones first.
		//
		if (o == null)
			return ASSIGNED_NULL;
		if (o instanceof String)
			return STRING;
		if (o instanceof java.sql.Date)
			return DATE;
		if (o instanceof java.util.Date)
			return DATE;
		if (o instanceof java.sql.Time)
			return TIME;
		if (o instanceof Timestamp)
			return TIMESTAMP;
		if (o instanceof Integer)
			return INT;
		if (o instanceof BigDecimal)
			return BIGDECIMAL;

		/*
		 * if (o instanceof AssiAssignedNull_S)) return ASSIGNED_NULL; if (o
		 * instanceof UnassignedNull_S)) return UNASSIGNED_NULL;
		 */

		if (o instanceof Byte)
			return BYTE;
		if (o instanceof Short)
			return SHORT;
		if (o instanceof Long)
			return LONG;

		if (o instanceof Double)
			return DOUBLE;
		if (o instanceof Float)
			return FLOAT;

		if (o instanceof Boolean)
			return BOOLEAN;

		if (o instanceof InputStream)
			return INPUTSTREAM;
		if (o instanceof Byte[])
			return BYTE_ARRAY;

		return OBJECT;
	}

	/**
	 * Returns the integer value that represents the specified type name. For
	 * example, a name value of "" returns an integer of 11.
	 *
	 * @param name
	 *          The name of a data type. Specify the name using one of the data
	 *          type constants of Variant. For example, BOOLEAN is the name of the
	 *          BOOLEAN constant for a boolean data type.
	 * @return The integer value that represents the specified type name. For
	 *         example, a name value of "" returns an integer of 11.
	 */
	public static int typeId(String name) {
		for (int i = NULL_TYPES + 1; i <= MaxTypes; i++)
			if (name.equals(typeName(i)))
				return i;
		return UNASSIGNED_NULL;
	}

	/**
	 * Returns the time zone offset, in milliseconds, of the current time zone.
	 * Used internally.
	 *
	 * @return The time zone offset, in milliseconds, of the current time zone.
	 */
	public static long getTimeZoneOffset() {
		if (!offsetsKnown) {
			java.sql.Date date = new java.sql.Date(70, 0, 1);
			timeZoneOffset = date.getTime();
			offsetsKnown = true;
		}
		return timeZoneOffset;
	}

	// setType is used to enforce type safe set operations.
	// setType never changes. If setType is set to non zero, type can only be
	// changed
	// to the same type as setType or to one of the null states.
	//
	private int setType;
	private int type;

	private Object value;

	/*
	 * private boolean booleanVal; private int intVal; private long longVal;
	 * private float floatVal; private double doubleVal;
	 *
	 * private String stringVal; private byte[] byteArrayVal; private BigDecimal
	 * bigDecimalVal; private java.sql.Date dateVal; private Time timeVal; private
	 * Timestamp timestampVal;
	 *
	 * private transient Object objectVal;
	 */

	private static String zeroString;
	private static BigDecimal zeroBIGDECIMAL;
	private static ByteArrayInputStream zeroBinary;
	private static byte[] zeroByteArray;
	private static boolean offsetsKnown;
	private static long timeZoneOffset;

	// ! private static long milliSecsPerDay = 24*60*60*1000;

	// !TODO. Get this functionality out of Variant!
	// !
	public final Object getDisplayValue() {
		switch (type) {
		case ASSIGNED_NULL:
		case UNASSIGNED_NULL:
			return "";
		case OBJECT:
			return getObject();
		case INPUTSTREAM:
			return getInputStream();
		default:
			break; // to make compiler happy
		}
		return toString();
	}

	private boolean setZeroValue(int unexpectedType, int expectedType) {
		if (zeroString == null) {
			zeroString = "";
			zeroBIGDECIMAL = new BigDecimal(0);
			zeroByteArray = new byte[0];
			zeroBinary = new ByteArrayInputStream(zeroByteArray);
		}
		switch (expectedType) {
		case INT:
			value = Integer.valueOf(0);
		case SHORT:
			value = Short.valueOf((short) 0);
		case BYTE:
			value = Byte.valueOf((byte) 0);
			break;
		case TIMESTAMP:
			value = new Timestamp(0);
			break;
		case TIME:
			value = new Time(0);
			break;
		case DATE:
			value = new java.sql.Date(0);
			break;
		case LONG:
			value = Long.valueOf(0);
			break;
		case BOOLEAN:
			value = Boolean.valueOf(false);
			break;
		case FLOAT:
			value = Float.valueOf(0);
			break;
		case DOUBLE:
			value = Double.valueOf(0);
			break;
		case STRING:
			value = zeroString;
			break;
		case BIGDECIMAL:
			value = zeroBIGDECIMAL;
			break;
		case OBJECT:
			value = null; // SS
			break;
		case INPUTSTREAM:
			value = zeroBinary;
			break;
		case BYTE_ARRAY:
			value = zeroByteArray;
			break;
		default:
			return false;
		}
		Diagnostic.check(unexpectedType <= NULL_TYPES);
		// Preserve null type.
		//
		type = unexpectedType;
		return true;
	}

	public interface TypeConverter {
		/**
		 * convertable muss true zurückgeben, wenn unexpectedType in expectedType
		 * konvertiert werden kann
		 *
		 * @param unexpectedType
		 *          int
		 * @param expectedType
		 *          int
		 * @return boolean
		 */
		boolean convertable(int unexpectedType, int expectedType);

		/**
		 * getter muss den unexpected in expected umwandeln und ins variant
		 * schreiben
		 *
		 * @param variant
		 *          Variant
		 */
		void getter(Variant variant);

		/**
		 * setter muss den value vom typ expectedType in unexpectedType umwandeln
		 *
		 * @param value
		 *          Object
		 */
		void setter(Object value, Variant variant);
	}

	static Vector<TypeConverter> typeConverters;

	public static void addTypeConverter(TypeConverter typeConverter) {
		if (typeConverters == null)
			typeConverters = new Vector<TypeConverter>();
		typeConverters.add(typeConverter);
	}

	public static void removeTypeConverter(TypeConverter typeConverter) {
		if (typeConverters != null) {
			typeConverters.remove(typeConverter);
			if (typeConverters.size() == 0)
				typeConverters = null;
		}
	}

	private boolean disableTypeCheck;

	private void typeProblem(int unexpectedType, int expectedType, boolean getter,
			Object value) {
		if (unexpectedType <= NULL_TYPES
				&& setZeroValue(unexpectedType, expectedType))
			return;

		if (disableTypeCheck)
			return;

		if (typeConverters != null) {
			disableTypeCheck = true;
			try {
				for (TypeConverter typeConverter : typeConverters) {
					if (typeConverter.convertable(unexpectedType, expectedType)) {
						if (getter) {
							typeConverter.getter(this);
						} else {
							typeConverter.setter(value, this);
						}
						return;
					}
				}
			} finally {
				disableTypeCheck = false;
			}
		}

		int str = getter ? ResIndex.UnexpectedTypeGet : ResIndex.UnexpectedType;

		VariantException.fire(Res.bundle.format(str,
				new String[] { typeName(unexpectedType), typeName(expectedType) }) + " "
				+ toStringDebug());
	}

	public final int getInt() {
		if (type != INT && type != SHORT && type != BYTE)
			typeProblem(type, INT, true, null);

		if (value == null)
			return 0;
		return ((Number) value).intValue();
	}

	public final short getShort() {
		if (type != BYTE && type != SHORT)
			typeProblem(type, SHORT, true, null);
		if (value == null)
			return 0;
		return ((Number) value).shortValue();
	}

	public final byte getByte() {
		if (type != BYTE)
			typeProblem(type, BYTE, true, null);
		if (value == null)
			return 0;
		return ((Number) value).byteValue();
	}

	public final long getLong() {
		if (type != LONG)
			typeProblem(type, LONG, true, null);
		if (value == null)
			return 0;
		return (Long) value;
	}

	public final boolean getBoolean() {
		if (type != BOOLEAN)
			typeProblem(type, BOOLEAN, true, null);
		if (value == null)
			return false;
		return (Boolean) value;
	}

	public final double getDouble() {
		if (type != DOUBLE)
			typeProblem(type, DOUBLE, true, null);
		if (value == null)
			return 0.0;
		return (Double) value;
	}

	public final float getFloat() {
		if (type != FLOAT)
			typeProblem(type, FLOAT, true, null);
		if (value == null)
			return 0.0f;
		return (Float) value;
	}

	public final String getAsString() {
		if (type == STRING)
			return (String) value;
		if (value == null || type == ASSIGNED_NULL || type == UNASSIGNED_NULL)
			return "";
		return value.toString();
	}

	public final String getString() {
		if (type != STRING)
			typeProblem(type, STRING, true, null);
		return (String) value;
	}

	public final BigDecimal getBigDecimal() {
		if (type != BIGDECIMAL)
			typeProblem(type, BIGDECIMAL, true, null);
		return (BigDecimal) value;
	}

	public final java.sql.Date getDate() {
		if (type != DATE)
			typeProblem(type, DATE, true, null);

		if (value instanceof Long)
			LocalDateUtil.setAsLocalDate(this, (Long) value, null);
		if (value instanceof java.sql.Date)
			return (java.sql.Date) value;
		if (value instanceof java.util.Date)
			return new java.sql.Date(((java.util.Date) value).getTime());
		return null;
	}

	public final Time getTime() {
		if (type != TIME)
			typeProblem(type, TIME, true, null);
		if (value instanceof Long)
			LocalDateUtil.setAsLocalTime(this, (Long) value, null);
		if (value instanceof Time)
			return (Time) value;
		return null;
	}

	public final Timestamp getTimestamp() {
		if (type != TIMESTAMP)
			typeProblem(type, TIMESTAMP, true, null);
		if (value instanceof Long)
			return new Timestamp((Long) value);
		if (value instanceof Timestamp)
			return (Timestamp) value;
		return null;
	}

	public final byte[] getByteArray() {
		if (type != BYTE_ARRAY && type != INPUTSTREAM && type != OBJECT)
			typeProblem(type, BYTE_ARRAY, true, null);

		if (value instanceof InputStream) {
			try {
				return InputStreamToByteArray.getBytes((InputStream) value);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (value instanceof byte[])
			return (byte[]) value;

		return null;
	}

	/**
	 * Retrieves the length of an array.
	 *
	 * @return Sets the length of an array.
	 */
	public final int getArrayLength() {
		byte[] b = getByteArray();
		if (b != null)
			return b.length;
		return 0;
	}

	/**
	 * @deprecated. Use getInputStream().
	 */
	public final InputStream getBinaryStream() {
		return getInputStream();
	}

	public final InputStream getInputStream() {
		if (type != INPUTSTREAM && type != OBJECT && type != BYTE_ARRAY)
			typeProblem(type, INPUTSTREAM, true, null);

		if (value instanceof byte[]) {
			byte[] b = (byte[]) value;
			setInputStream(new ByteArrayInputStream(b, 0, b.length));
		}
		Diagnostic.check(value == null || value instanceof InputStream);
		return (InputStream) value;
	}

	protected boolean wrongSetType(int defType) {
		return (setType != 0 && setType != OBJECT && setType != defType);
	}

	protected boolean wrongSetType(int defTypeFrom, int defTypeTo) {
		return (setType != 0 && setType != OBJECT && (setType < defTypeFrom || setType > defTypeTo));
	}

	public final void setInt(int val) {
		Diagnostic.check(this != nullVariant);
		if (wrongSetType(INT))
			typeProblem(setType, INT, false, val);
		type = INT;
		value = Integer.valueOf(val);
	}

	public final void setShort(short val) {
		if (wrongSetType(BYTE, LONG))
			typeProblem(setType, SHORT, false, val);
		type = SHORT;
		value = Short.valueOf(val);
	}

	public final void setByte(byte val) {
		if (wrongSetType(BYTE, LONG))
			typeProblem(setType, BYTE, false, val);
		type = BYTE;
		value = Byte.valueOf(val);
	}

	// !/*
	// ! public final void setAsInt(int val) {
	// ! switch (type) {
	// ! case BYTE: setByte((byte)val); return;
	// ! case SHORT: setShort((short)val); return;
	// ! default: setInt(val); return;
	// ! }
	// ! }
	// !
	// !*/

	public final void setLong(long val) {
		if (wrongSetType(BYTE, LONG))
			typeProblem(setType, LONG, false, val);
		type = LONG;
		value = Long.valueOf(val);
	}

	public final void setBoolean(boolean val) {
		if (wrongSetType(BOOLEAN))
			typeProblem(setType, BOOLEAN, false, val);
		type = BOOLEAN;
		value = Boolean.valueOf(val);
	}

	public final void setDouble(double val) {
		if (wrongSetType(DOUBLE))
			typeProblem(setType, DOUBLE, false, val);
		type = DOUBLE;
		value = Double.valueOf(val);
	}

	public final void setFloat(float val) {
		if (wrongSetType(FLOAT))
			typeProblem(setType, FLOAT, false, val);
		type = FLOAT;
		value = Float.valueOf(val);
	}

	// ! /*
	// ! public final void setAsDouble(double val) {
	// ! switch(setType) {
	// ! case FLOAT: setFloat((float)val); return;
	// ! case DOUBLE: setDouble(val); return;
	// ! default: break; // to make compiler happy
	// ! }
	// ! typeProblem(setType, DOUBLE, false);
	// ! }
	// !*/

	public final void setString(String val) {
		if (wrongSetType(STRING))
			typeProblem(setType, STRING, false, val);
		if (val == null)
			type = ASSIGNED_NULL;
		else
			type = STRING;
		value = val;
	}

	public final void setBigDecimal(BigDecimal val) {
		if (wrongSetType(BIGDECIMAL))
			typeProblem(setType, BIGDECIMAL, false, val);
		type = val == null ? ASSIGNED_NULL : BIGDECIMAL;
		value = val;
	}

	// !/*
	// ! public final void setBigDecimal(String val) {
	// ! if (setType != BIGDECIMAL && setType != 0)
	// ! typeProblem(setType, BIGDECIMAL, false);
	// ! type = val == null ? ASSIGNED_NULL : BIGDECIMAL;
	// ! bigDecimalVal = null;
	// ! stringVal = val;
	// ! booleanVal = true;
	// ! }
	// !*/

	public final void setDate(java.sql.Date val) {
		if (wrongSetType(DATE))
			typeProblem(setType, DATE, false, val);
		if (val == null) {
			type = ASSIGNED_NULL;
			value = null;
		} else {
			type = DATE;
			value = new java.sql.Date(val.getTime());
		}
	}

	public final void setDate(java.util.Date val) {
		if (wrongSetType(DATE))
			typeProblem(setType, DATE, false, val);
		if (val == null) {
			type = ASSIGNED_NULL;
			value = null;
		} else {
			type = DATE;
			value = new java.sql.Date(val.getTime());
		}
	}

	public final void setTime(Time val) {
		if (wrongSetType(TIME))
			typeProblem(setType, TIME, false, val);
		if (val == null) {
			type = ASSIGNED_NULL;
			value = null;
		} else {
			type = TIME;
			value = new java.sql.Time(val.getTime());
		}
	}

	public final void setTimestamp(Timestamp val) {
		if (wrongSetType(TIMESTAMP))
			typeProblem(setType, TIMESTAMP, false, val);
		if (val == null) {
			type = ASSIGNED_NULL;
			value = null;
		} else {
			type = TIMESTAMP;
			value = new Timestamp(val.getTime());
		}
	}

	public final void setDate(long val) {
		if (wrongSetType(DATE))
			typeProblem(setType, DATE, false, val);
		type = DATE;
		value = new Date(val);
	}

	public final void setTime(long val) {
		if (wrongSetType(TIME))
			typeProblem(setType, TIME, false, val);
		type = TIME;
		value = new Time(val);
	}

	/**
	 * Sets the value of the Variant as a Timestamp value.
	 *
	 * @param val
	 *          The new value as a <b>long</b> value.
	 */
	public final void setTimestamp(long val) {
		if (wrongSetType(TIMESTAMP))
			typeProblem(setType, TIMESTAMP, false, val);
		type = Variant.TIMESTAMP;

		value = new Timestamp(val);
	}

	/**
	 * Sets the value of the Variant to a new array of bytes.
	 *
	 * @param val
	 *          The new array of bytes that becomes the new value of this Variant.
	 * @param length
	 *          The length of the new byte array.
	 */
	public final void setByteArray(byte[] val, int length) {
		setByteArray(BYTE_ARRAY, val, length);
	}

	public final void setByteArray(byte[] val) {
		setByteArray(BYTE_ARRAY, val, val == null ? 0 : val.length);
	}

	/**
	 * Sets the value of the Variant to a new array of bytes.
	 *
	 * @param valType
	 *          Variant data type for val param.
	 * @param val
	 *          The new array of bytes that becomes the new value of this Variant.
	 * @param length
	 *          The length of the new byte array.
	 */
	public final void setByteArray(int valType, byte[] val, int length) {
		if (setType != BYTE_ARRAY && setType != INPUTSTREAM && setType != OBJECT
				&& setType != 0)
			typeProblem(setType, BYTE_ARRAY, false, val);

		if (val == null)
			type = ASSIGNED_NULL;
		else if (setType != 0)
			type = setType;
		else
			type = valType;

		if (val != null && val.length != length) {
			byte[] b = new byte[length];
			System.arraycopy(val, 0, b, 0, Math.min(val.length, length));
			val = b;
		}
		value = val;
	}

	/**
	 * Sets the length of an array.
	 *
	 * @param length
	 *          The length of an array.
	 */
	public final void setArrayLength(int length) {
		byte[] val = getByteArray();
		if (val == null)
			val = new byte[length];
		else if (val != null && val.length != length) {
			byte[] b = new byte[length];
			System.arraycopy(val, 0, b, 0, Math.min(val.length, length));
			value = b;
		}
	}

	/**
	 * @deprecated. Use setInputStream().
	 */
	public final void setBinaryStream(InputStream val) {
		setInputStream(val);
	}

	public final void setInputStream(InputStream val) {
		setInputStream(INPUTSTREAM, val);
	}

	public final void setInputStream(int valType, InputStream val) {
		if (setType != INPUTSTREAM && setType != BYTE_ARRAY && setType != OBJECT
				&& setType != 0)
			typeProblem(setType, INPUTSTREAM, false, val);

		if (val == null)
			type = ASSIGNED_NULL;
		else if (setType != 0)
			type = setType;
		else
			type = INPUTSTREAM;

		value = val;
	}

	private final void setBlob(Variant variant) {
		Diagnostic.check(setType == 0 || setType == variant.type);
		type = variant.type;
		value = variant.value;
	}

	public final void setVariant(Variant variant) {
		if (variant == null) {
			setAssignedNull();
			return;
		}
		switch (variant.type) {
		case STRING:
			setString((String) variant.value);
			break;

		case BYTE:
			setByte((Byte) variant.value);
			break;
		case SHORT:
			setShort((Short) variant.value);
			break;
		case INT:
			setInt((Integer) variant.value);
			break;
		case BOOLEAN:
			setBoolean((Boolean) variant.value);
			break;
		case TIMESTAMP:
			setTimestamp(variant.getTimestamp());
			break;
		case DATE:
			setDate(variant.getDate());
			break;
		case TIME:
			setTime(variant.getTime());
			break;

		case LONG:
			setLong((Long) variant.value);
			break;
		case FLOAT:
			setFloat((Float) variant.value);
			break;
		case DOUBLE:
			setDouble((Double) variant.value);
			break;
		case BIGDECIMAL:
			// ! if (value.booleanVal)
			// ! setBigDecimal(value.stringVal);
			// ! else
			setBigDecimal((BigDecimal) variant.value);
			break;
		case INPUTSTREAM:
			if (setType == 0 || setType == variant.type)
				setBlob(variant);
			else
				setInputStream(variant.getInputStream());
			break;
		case BYTE_ARRAY:
			if (setType == 0 || setType == variant.type)
				setBlob(variant);
			else
				setByteArray(variant.getByteArray());
			break;
		case OBJECT:
			if (setType == 0 || setType == variant.type)
				setBlob(variant);
			else
				setObject(variant.getObject());
			break;

		case ASSIGNED_NULL:
		case UNASSIGNED_NULL:
			if (setType != variant.type && setType != 0)
				typeProblem(variant.type, setType, false, null);
			type = variant.type;
			value = null;
			break;
		default:
			invalidVariantType(variant.type);
			// ! Diagnostic.println("putVariant() Invalid type: "+value.type);
			break;
		}
	}

	/**
	 * @since 2.01 Set this variant to value. If value is not the same setType,
	 *        then an attempt is made to convert to the data type of this variant.
	 */

	public final void setAsVariant(Variant value) {
		if (value == null || value.isNull()) {
			setAssignedNull();
			return;
		}

		switch (setType) {
		case STRING:
			setString(value.toString());
			break;

		case BYTE:
			setByte((byte) value.getAsInt());
			break;
		case SHORT:
			setShort((short) value.getAsInt());
			break;
		case INT:
			setInt(value.getAsInt());
			break;
		case BOOLEAN:
			setBoolean(value.getAsBoolean());
			break;

		case TIMESTAMP:
			setAsTimestamp(value);
			break;
		case DATE:
			setAsDate(value);
			break;
		case TIME:
			setAsTime(value);
			break;
		case LONG:
			setLong(value.getAsLong());
			break;

		case FLOAT:
			setFloat(value.getAsFloat());
			break;
		case DOUBLE:
			setDouble(value.getAsDouble());
			break;
		case BIGDECIMAL:
			setBigDecimal(value.getAsBigDecimal());
			break;
			// ! case UNASSIGNED_NULL: setUnassignedNull(); break; //A Variant without a
			// setType has setType==0
		case ASSIGNED_NULL:
			setAssignedNull();
			break;
		case OBJECT:
			setObject(value.getAsObject());
			break;
		case INPUTSTREAM:
			if (value.getType() == STRING)
				setInputStream(new ByteArrayInputStream(value.getString().getBytes()));
			else if (value.getType() == INPUTSTREAM)
				setInputStream(value.getInputStream());
			else if (value.getType() == BYTE_ARRAY)
				setInputStream(new ByteArrayInputStream(value.getByteArray()));
			else
				invalidVariantType(setType);
			break;
			// ! case BYTE_ARRAY: missing implementation!!!
		default:
			if (setType == 0 || setType == value.type || value.isNull())
				setVariant(value);
			else
				invalidVariantType(setType);
			break;
		}
	}

	/**
	 * Attempts to parse the passed string s to the type indicated by wantedType.
	 * Date values must be of the format "yyyy-mm-dd". Time values must be of the
	 * format "hh:mm:ss". Timestamp values must be of the format
	 * "yyyy-mm-dd hh:mm:ss.fffffffff", where f indicates a digit of the fractions
	 * of seconds. Boolean values are true for true, anything else is false.
	 *
	 * @param wantedType
	 * @param s
	 */
	public final void setFromString(int wantedType, String s) {
		if (wrongSetType(wantedType))
			typeProblem(setType, wantedType, false, s);

		if (s == null)
			type = STRING; // Force use of setString method

		switch (wantedType) {
		case BYTE:
			setByte(Byte.parseByte(s));
			break;
		case SHORT:
			setShort(Short.parseShort(s));
			break;
		case INT:
			setInt(Integer.parseInt(s));
			break;
		case LONG:
			setLong(Long.parseLong(s));
			break;

		case FLOAT:
			setFloat(Float.valueOf(s).floatValue());
			break;
		case DOUBLE:
			setDouble(Double.valueOf(s).doubleValue());
			break;
		case BIGDECIMAL:
			setBigDecimal(new BigDecimal(s));
			break;

		case BOOLEAN:
			setBoolean(Boolean.valueOf(s).booleanValue());
			break;

		case TIMESTAMP:
			type = TIMESTAMP;
			setTimestamp(java.sql.Timestamp.valueOf(s));
			break;
		case DATE:
			LocalDateUtil.setLocalDateAsLong(this, s);
			break;
		case TIME:
			LocalDateUtil.setLocalTimeAsLong(this, s);
			break;

		case STRING:
			setString(s);
			break;

		default:
			invalidVariantType(type);
			break;
		}
	}

	private final void invalidVariantType(int type) {
		VariantException.fire(Res.bundle.format(ResIndex.InvalidVariantType,
				new String[] { typeName(type) }) + " " + toStringDebug());
	}

	public final void setObject(Object val) {
		if (wrongSetType(OBJECT))
			typeProblem(setType, OBJECT, false, val);
		if (val == null)
			type = ASSIGNED_NULL;
		else
			type = OBJECT;
		value = val;
	}

	public final Object getObject() {
		if (type != OBJECT && type != BYTE_ARRAY && type != INPUTSTREAM)
			typeProblem(type, OBJECT, true, null);
		return value;
	}

	Object getObjectInternal() {
		return value;
	}

	public final short getAsShort() {
		switch (type) {
		case BYTE:
			return ((Byte) value).shortValue();
		case SHORT:
			return ((Short) value).shortValue();
		case INT:
			return ((Integer) value).shortValue();
		case BOOLEAN:
			return getBoolean() ? (short) 1 : (short) 0;
		case TIME:
		case DATE:
		case TIMESTAMP:
			return (short) getAsLong();
		case LONG:
			return ((Long) value).shortValue();
		case FLOAT:
			return ((Float) value).shortValue();
		case DOUBLE:
			return ((Double) value).shortValue();
		case BIGDECIMAL:
			return (short) getBigDecimal().intValue();
		case Variant.UNASSIGNED_NULL:
		case Variant.ASSIGNED_NULL:
			return 0;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, SHORT, true, null);
		return 0;
	}

	public final int getAsInt() {
		switch (type) {
		case BYTE:
			return (Byte) value;
		case SHORT:
			return (Short) value;
		case INT:
			return (Integer) value;
		case BOOLEAN:
			return getBoolean() ? 1 : 0;
		case TIME:
			return (int) getTime().getTime();
		case DATE:
			return (int) getDate().getTime();
		case TIMESTAMP:
			return (int) getAsLong();
		case LONG:
			return ((Long) value).intValue();
		case FLOAT:
			return ((Float) value).intValue();
		case DOUBLE:
			return ((Double) value).intValue();
		case BIGDECIMAL:
			return getBigDecimal().intValue();
		case Variant.UNASSIGNED_NULL:
		case Variant.ASSIGNED_NULL:
			return 0;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, INT, true, null);
		return 0;
	}

	public final long getAsLong() {
		switch (type) {
		case BOOLEAN:
			return getBoolean() ? 1 : 0;
		case BYTE:
			return ((Byte) value).longValue();
		case SHORT:
			return ((Short) value).longValue();
		case INT:
			return ((Integer) value).longValue();
		case LONG:
			return ((Long) value).longValue();
		case FLOAT:
			return ((Float) value).longValue();
		case DOUBLE:
			return ((Double) value).longValue();
		case BIGDECIMAL:
			return getBigDecimal().longValue();
		case TIME:
			return getTime().getTime();
		case DATE:
			return getDate().getTime();
		case TIMESTAMP: {
			return getTimestamp().getTime();
		}
		case Variant.UNASSIGNED_NULL:
		case Variant.ASSIGNED_NULL:
			return 0;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, LONG, true, null);
		return 0;
	}

	public final double getAsDouble() {
		switch (type) {
		case BYTE:
			return ((Byte) value).doubleValue();
		case SHORT:
			return ((Short) value).doubleValue();
		case INT:
			return ((Integer) value).doubleValue();
		case LONG:
			return ((Long) value).doubleValue();
		case FLOAT:
			return ((Float) value).doubleValue();
		case DOUBLE:
			return ((Double) value).doubleValue();
		case BIGDECIMAL:
			return getBigDecimal().doubleValue();
		case TIME:
		case DATE:
		case TIMESTAMP:
			return getAsLong();
		case Variant.UNASSIGNED_NULL:
		case Variant.ASSIGNED_NULL:
			return 0;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, DOUBLE, true, null);
		return 0;
	}

	public final float getAsFloat() {
		switch (type) {
		case BYTE:
			return ((Byte) value).floatValue();
		case SHORT:
			return ((Short) value).floatValue();
		case INT:
			return ((Integer) value).floatValue();
		case LONG:
			return ((Long) value).floatValue();
		case FLOAT:
			return ((Float) value).floatValue();
		case DOUBLE:
			return ((Double) value).floatValue();
		case BIGDECIMAL:
			return getBigDecimal().floatValue();
		case TIME:
		case DATE:
		case TIMESTAMP:
			return getAsLong();
		case Variant.UNASSIGNED_NULL:
		case Variant.ASSIGNED_NULL:
			return 0;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, FLOAT, true, null);
		return 0;
	}

	public final BigDecimal getAsBigDecimal() {
		switch (type) {
		case BYTE:
		case SHORT:
		case INT:
			return new BigDecimal(getAsInt());
		case LONG:
			return BigDecimal.valueOf(getAsLong(), 0); // !JOAL BugFix, otherwise the
			// double constructor is used,
			// which does not have enough
			// precision to hold a long.
		case FLOAT:
			return new BigDecimal((Float) value);
			// ! case DOUBLE: return new BigDecimal(doubleVal, 4); //! JDK beta 3.2
		case DOUBLE:
			return new BigDecimal((Double) value);
		case BIGDECIMAL:
			return getBigDecimal();
		case TIME:
		case DATE:
		case TIMESTAMP:
			return BigDecimal.valueOf(getAsLong());
		case Variant.UNASSIGNED_NULL:
		case Variant.ASSIGNED_NULL:
			return new BigDecimal(0);
		default:
			break; // to make compiler happy
		}
		typeProblem(type, BIGDECIMAL, true, null);
		return null;
	}

	public final boolean getAsBoolean() {
		switch (type) {
		case BOOLEAN:
			return (Boolean) value;
		case STRING:
			return Boolean.valueOf(getString()).booleanValue();
		case BYTE:
		case SHORT:
		case INT:
			return getAsInt() != 0;
		case LONG:
			return getLong() != 0;
		case FLOAT:
			return getFloat() != 0;
		case DOUBLE:
			return getDouble() != 0;
		case BIGDECIMAL:
			return getBigDecimal().doubleValue() != 0;
		case TIME:
		case DATE:
		case TIMESTAMP:
			return getAsLong() != 0;
		}
		return false;
	}

	/**
	 * Sets Time to value.
	 *
	 * @param value
	 *          if value is of type TIME, value is copied directly. if value is of
	 *          type BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIGDECIMAL,
	 *          DATE, TIMESTAMP, setTimeStamp() is called with the return value
	 *          from value.getAsLong() If value is of type ASSIGNED_NULL or
	 *          UNASSIGNED_NULL, this is set to the same *_NULL value.
	 */
	public final void setAsTime(Variant value) {
		switch (value.type) {
		case TIME:
			setTime(value.getTime());
			return;
		case BOOLEAN:
		case BYTE:
		case SHORT:
		case INT:
		case LONG:
		case FLOAT:
		case DOUBLE:
		case BIGDECIMAL:
		case DATE:
		case TIMESTAMP:
			setTime(value.getAsLong());
			return;
		case Variant.UNASSIGNED_NULL:
			setUnassignedNull();
			return;
		case Variant.ASSIGNED_NULL:
			setAssignedNull();
			return;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, TIME, false, value);
	}

	/**
	 * Sets Timestamp to value.
	 *
	 * @param value
	 *          if value is of type TIME, value is copied directly. if value is of
	 *          type BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIGDECIMAL,
	 *          DATE, TIME, setTimeStamp() is called with the return value from
	 *          value.getAsLong() If value is of type ASSIGNED_NULL or
	 *          UNASSIGNED_NULL, this is set to the same *_NULL value.
	 */

	public final void setAsTimestamp(Variant value) {
		switch (value.type) {
		case TIMESTAMP:
			setTimestamp(value.getTimestamp());
			return;
		case BOOLEAN:
		case BYTE:
		case SHORT:
		case INT:
		case LONG:
		case FLOAT:
		case DOUBLE:
		case BIGDECIMAL:
			setTimestamp(value.getAsLong());
			return;
		case DATE: // RAID139953
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(value.getLong());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			setDate(cal.getTimeInMillis());
			return;
		case TIME: // RAID139953
			long lvalue = value.getAsLong();
			lvalue = lvalue % 86400000;
			if (lvalue < 0)
				lvalue += 86400000;
			setTimestamp(lvalue);
			return;
		case Variant.UNASSIGNED_NULL:
			setUnassignedNull();
			return;
		case Variant.ASSIGNED_NULL:
			setAssignedNull();
			return;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, TIMESTAMP, false, value);
	}

	public final void setAsDate(Variant value) {
		switch (value.type) {
		case DATE:
			setDate(value.getDate());
			return;
		case BOOLEAN:
		case BYTE:
		case SHORT:
		case INT:
		case LONG:
		case FLOAT:
		case DOUBLE:
		case BIGDECIMAL:
		case TIME:
		case TIMESTAMP:
			setDate(value.getAsLong());
			return;
		case Variant.UNASSIGNED_NULL:
			setUnassignedNull();
			return;
		case Variant.ASSIGNED_NULL:
			setAssignedNull();
			return;
		default:
			break; // to make compiler happy
		}
		typeProblem(type, DATE, false, value);
	}

	public final void setNull(int nullType) {
		// Test makes sure we set it to something reasonable.
		//
		if (nullType == UNASSIGNED_NULL)
			this.type = UNASSIGNED_NULL;
		else {
			Diagnostic.check(nullType == ASSIGNED_NULL);
			this.type = ASSIGNED_NULL;
		}
		value = null;
	}

	/**
	 * Sets the value of the Variant as an assigned <b>null</b>. An assigned
	 * <b>null</b> is a value that has been explicitly set to <b>null</b> in
	 * contrast to one that is simply unassigned.
	 */
	public final void setAssignedNull() {
		this.type = ASSIGNED_NULL;
		value = null;
	}

	public final void setUnassignedNull() {
		this.type = UNASSIGNED_NULL;
		value = null;
	}

	public final boolean isAssignedNull() {
		return type == ASSIGNED_NULL;
	}

	public final boolean isUnassignedNull() {
		return type == UNASSIGNED_NULL;
	}

	public final boolean isNull() {
		return type <= NULL_TYPES;
	}

	public final int getType() {
		return type;
	}

	public final int getSetType() {
		return setType;
	}

	// used by dbSwing components (dcy)
	public Object getAsObject() {
		switch (type) {
		case ASSIGNED_NULL:
		case UNASSIGNED_NULL:
			return null;
		case BOOLEAN:
		case INT:
		case BYTE:
		case SHORT:
		case FLOAT:
		case DOUBLE:
		case LONG:
			return value;
		case BIGDECIMAL:
			return getBigDecimal();
		case STRING:
			if (value == null)
				return "";
			return value.toString();
		case DATE:
			return new java.sql.Date(getDate().getTime());
		case TIME:
			return new Time(getTime().getTime());
		case TIMESTAMP:
			return getTimestamp();
		case BYTE_ARRAY:
			return getByteArray();
		case OBJECT:
			return value;
		case INPUTSTREAM:
			return getInputStream();
		default:
			break; // to make compiler happy
		}
		Diagnostic.fail();
		return null;
	}

	void setObjectInt(Object object, int variantType) {
		type = variantType;
		value = object;
	}

	// ! used by dbSwing components (dcy)

	/**
	 * Currently used by dbSwing components to set a Variant from an Object.
	 *
	 * @param object
	 *          The value to set.
	 * @param variantType
	 *          Variant data type that the object maps to. For example, if the
	 *          object is of type Integer, then variantType should be Variant.INT.
	 */
	public void setAsObject(Object object, int variantType) {
		if (object == null) {
			setAssignedNull();
			return;
		}
		switch (variantType) {
		case Variant.ASSIGNED_NULL:
			setAssignedNull();
			break;
		case Variant.UNASSIGNED_NULL:
			setUnassignedNull();
			break;
		case Variant.INT:
			setInt(((Integer) object).intValue());
			break;
		case Variant.BYTE:
			setByte(((Byte) object).byteValue());
			break;
		case Variant.SHORT:
			setShort(((Short) object).shortValue());
			break;
		case Variant.FLOAT:
			setFloat(((Float) object).floatValue());
			break;
		case Variant.DOUBLE:
			setDouble(((Double) object).doubleValue());
			break;
		case Variant.LONG:
			setLong(((Long) object).longValue());
			break;
		case Variant.BIGDECIMAL:
			setBigDecimal((BigDecimal) object);
			break;
		case Variant.BOOLEAN:
			setBoolean(((Boolean) object).booleanValue());
			break;
		case Variant.STRING:
			// ! use toString() instead of (String) cast per
			// ! raid 144293
			// !
			setString(object.toString());
			break;
		case Variant.DATE:
			if (object instanceof java.sql.Date)
				setDate((java.sql.Date) object);
			else if (object instanceof Long)
				setDate(new java.util.Date((Long) object));
			else
				setDate((java.util.Date) object);
			break;
		case Variant.TIME:
			setTime((Time) object);
			break;
		case Variant.TIMESTAMP:
			setTimestamp((Timestamp) object);
			break;
		case Variant.BYTE_ARRAY:
			if (object instanceof Blob)
				try {
					setByteArray(
							((Blob) object).getBytes(0, (int) ((Blob) object).length()));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			else if (object instanceof InputStream)
				setByteArray(
						new InputStreamToByteArray((InputStream) object).getBytes());
			else
				setByteArray((byte[]) object, ((byte[]) object).length);
			break;
		case Variant.OBJECT:
			setObject(object);
			break;
		case Variant.INPUTSTREAM:
			if (object instanceof Blob)
				try {
					setInputStream(((Blob) object).getBinaryStream());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			else if (object instanceof byte[])
				setInputStream(new InputStreamToByteArray((byte[]) object));
			else
				setInputStream((InputStream) object);
			break;
		default:
			break; // to make compiler happy
		}
	}

	@Override
	public final String toString() {
		switch (type) {
		case ASSIGNED_NULL:
		case UNASSIGNED_NULL:
			return "";
		case INT:
			return Integer.toString(getInt(), 10);
		case BYTE:
			return Integer.toString(getByte(), 10);
		case SHORT:
			return Integer.toString(getShort(), 10);
		case FLOAT:
			return ((Float) value).toString();
		case DOUBLE:
			return ((Double) value).toString();
		case LONG:
			return ((Long) value).toString();
		case BIGDECIMAL:
			return getBigDecimal().toString();
		case BOOLEAN:
			return ((Boolean) value).toString();
		case STRING:
			return getString();
		case DATE:
			return getDate().toString();
		case TIME:
			return getTime().toString();
		case TIMESTAMP:
			return getTimestamp().toString();
		case BYTE_ARRAY:
			byte[] b = getByteArray();
			if (b == null)
				return "";
			return new String(b);
		case OBJECT:
			if (value == null)
				return "";
			return value.toString();
		case INPUTSTREAM:
			getInputStream();
			if (value == null)
				return "";
			return value.toString();
		default:
			break; // to make compiler happy
		}
		Diagnostic.fail();
		return "";
	}

	public String toStringDebug() {
		return "VariantType=" + typeName(type) + " Value=" + toString();
	}

	/**
	 * @since JB2.0 Returns true if value or value instance changed. Note that
	 *        will return false for Variants storing different Object reference
	 *        values that may be equal. Provides high speed test that indicates
	 *        two variants may not be equal. If true is returned they are equal.
	 *        If false is returned, they might still be equal.
	 */
	/**
	 * Returns <b>true</b> if the value or value instance changed. Returns
	 * <b>false</b> for Variants storing different object reference values that
	 * may be equal. Provides a high speed test that indicates if two variants are
	 * equal. If <b>true</b> is returned, they are equal. If false is returned,
	 * they might still be equal.
	 *
	 * @param value2
	 * @return <b>true</b> if the value or value instance changed.
	 */
	public boolean equalsInstance(Variant value2) {
		if (type == value2.type) {
			switch (type) {
			case Variant.INPUTSTREAM:
				return getInputStream() == value2.getInputStream();
			case Variant.OBJECT:
				return getObject() == value2.getObject();
			}
		}
		return equals(value2);
	}

	/**
	 * Determines whether a Variant value is equal to this Variant value. If
	 * equals() returns <b>true</b>, the two Variant values are of the same type
	 * and are equal in value. A returned value of <b>false</b> indicates that the
	 * two values differ in value or type.
	 *
	 * @param variant
	 *          The Variant value being compared to the data type and value of
	 *          this Variant.
	 * @return <b>true</b> if the two Variant values are of the same type and are
	 *         equal in value. A returned value of <b>false</b> indicates that the
	 *         two values differ in value or type.
	 */
	public final boolean equals(Variant variant) {

		if (variant == this)
			return true;

		if (variant == null)
			return false;

		if (type != variant.type) {
			if (type <= NULL_TYPES || variant.type <= NULL_TYPES)
				return type <= NULL_TYPES == variant.type <= NULL_TYPES;
			typeProblem(variant.type, type, true, variant);
		}

		switch (type) {
		case ASSIGNED_NULL:
		case UNASSIGNED_NULL:
			return variant.type == type;
		case INT:
		case BYTE:
		case SHORT:
			return getInt() == variant.getInt();
		case BOOLEAN:
			return getBoolean() == variant.getBoolean();
		case FLOAT:
			return getFloat() == variant.getFloat();
		case DOUBLE:
			return getDouble() == variant.getDouble();
		case TIMESTAMP:
			return compareTimestamps(this, variant) == 0;
		case DATE:
			return compareDates(this, variant) == 0;
		case TIME:
			return compareTimes(this, variant) == 0;
		case LONG:
			return ((Long) value).equals(variant.value);
		case BIGDECIMAL:
			return getBigDecimal().equals(variant.getBigDecimal());
		case STRING:
			return getString().equals(variant.getString());
		case BYTE_ARRAY:
			return Arrays.equals(getByteArray(), variant.getByteArray());
		case INPUTSTREAM:
			return equals(getInputStream(), variant.getInputStream());
		case OBJECT:
			return getObject().equals(variant.getObject());
		default:
			break; // to make compiler happy
		}
		Diagnostic.fail();
		return false;
	}

	// ! static int bugCount;

	private boolean equals(InputStream stream1, InputStream stream2) {
		if (stream1 == stream2)
			return true;
		if (stream1 == null || stream2 == null)
			return false;

		// Cannot compare, so assume not equal.
		//
		if (!stream1.markSupported() || !stream2.markSupported())
			return false;

		try {
			stream1.reset();
			stream2.reset();
			// ! int count = 0;
			// ! Diagnostic.println("stream1 length: " + stream1.available());
			// ! Diagnostic.println("stream2 length: " + stream2.available());

			int count = 1;
			int count2;
			byte[] buf = new byte[1024];
			byte[] buf2 = new byte[1024];
			while (count > 0) {

				count = stream1.read(buf);
				count2 = stream2.read(buf2);
				// ! ++count;
				if (count != count2) {
					// ! Diagnostic.println(count +" ch: "+ch+" ch2: "+ch2);
					// ! Diagnostic.println("mismatch: "+stream1+" "+stream2);
					return false;
				}
				for (int index = 0; index < count; ++index) {
					if (buf[index] != buf2[index])
						return false;
				}
			}
		} catch (IOException ex) {
			Diagnostic.println("IOException hit:");
			Diagnostic.printStackTrace(ex);
			return false;
		}
		return true;
	}

	private static final int compareInt(int value1, int value2) {
		if (value1 < value2)
			return -1;
		if (value1 > value2)
			return 1;
		return 0;
	}

	private static final int compareLong(long value1, long value2) {
		if (value1 < value2)
			return -1;
		if (value1 > value2)
			return 1;
		return 0;
	}

	private static final int compareDouble(double value1, double value2) {
		if (value1 < value2)
			return -1;
		if (value1 > value2)
			return 1;
		return 0;
	}

	private static final int compareFloat(float value1, float value2) {
		if (value1 < value2)
			return -1;
		else if (value1 > value2)
			return 1;
		else
			return 0;
	}

	// Comparing some part of dates:
	private static final int compareCalendarFields(Variant value1, Variant value2,
			int[] fields) {
		GregorianCalendar cal1 = new GregorianCalendar();
		GregorianCalendar cal2 = new GregorianCalendar();
		cal1.setTimeInMillis(value1.getAsLong());
		cal2.setTimeInMillis(value2.getAsLong());
		for (int field : fields) {
			int field1 = cal1.get(field);
			int field2 = cal2.get(field);
			if (field1 < field2)
				return -1;
			else if (field1 > field2)
				return 1;
		}
		return 0;
	}

	// Remove the time portion when comparing dates:
	private static final int compareDates(Variant value1, Variant value2) {
		return compareCalendarFields(value1, value2,
				new int[] { Calendar.YEAR, Calendar.DAY_OF_YEAR });
	}

	// Remove the time portion when comparing times:
	private static final int compareTimes(Variant value1, Variant value2) {
		return compareCalendarFields(value1, value2,
				new int[] { Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND,
						Calendar.MILLISECOND });
	}

	private static int compareTimestamps(Variant value1, Variant value2) {
		int result = compareLong(((Timestamp) value1.value).getTime(),
				((Timestamp) value2.value).getTime());
		return result;
	}

	private final int compareBoolean(boolean bool1, boolean bool2) {
		if (bool1 == bool2)
			return 0;
		if (bool1)
			return 1;
		return -1;
	}

	/**
	 * Compares a Variant value to the value of this Variant, returning the
	 * result. If the result is zero, the two Variants are equal. If the returned
	 * value is less than zero (a negative integer), the value of this Variant is
	 * less than value2. If the returned value is greater than zero (a positive
	 * integer), the value of this Variant is greater than value2.
	 *
	 * @param value2
	 *          The value this Variant is being compared to.
	 * @return Zero if the two Variants are equal. Less than zero (a negative
	 *         integer) if this Variant is less than value2. Greater than zero (a
	 *         positive integer) if the value of this Variant is greater than
	 *         value2.
	 */
	public int compareTo(Variant value2) {
		if (isNull())
			return value2.isNull() ? 0 : -1;
		if (value2.isNull())
			return 1;

		switch (type) {
		case Variant.BYTE:
		case Variant.SHORT:
		case Variant.INT:
			return compareInt(getInt(), value2.getAsInt());

		case Variant.LONG:
			return compareLong(getLong(), value2.getAsLong());
		case Variant.FLOAT:
			return compareFloat(getFloat(), value2.getAsFloat());
		case Variant.DOUBLE:
			return compareDouble(getDouble(), value2.getAsDouble());
		case Variant.BIGDECIMAL:
			return getBigDecimal().compareTo(value2.getAsBigDecimal());

		case Variant.DATE:
			return compareDates(this, value2);
		case Variant.TIME:
			return compareTimes(this, value2);
		case Variant.TIMESTAMP:
			return compareTimestamps(this, value2);

		case Variant.BOOLEAN:
			return compareBoolean(getBoolean(), value2.getAsBoolean());

		case Variant.STRING:
			return getString().compareTo(value2.toString());
		default:
			break; // to make compiler happy
		}

		Diagnostic.fail();
		return 0;
	}

	/**
	 * Adds a value to this Variant, storing the result in the result parameter.
	 *
	 * @param value2
	 *          The value added to this Variant.
	 * @param result
	 *          The result of the two Variant values added together.
	 */
	public void add(Variant value2, Variant result) {
		if (value2.isNull() && isNull())
			result.setVariant(this);
		else {
			switch (type) {
			case Variant.BYTE:
				result.setByte((byte) (getAsInt() + value2.getAsInt()));
				break;
			case Variant.SHORT:
				result.setShort((short) (getAsInt() + value2.getAsInt()));
				break;
			case Variant.INT:
				result.setInt(getInt() + value2.getAsInt());
				break;
			case Variant.LONG:
				result.setLong(getLong() + value2.getAsLong());
				break;
			case Variant.DATE:
				result.setDate(getDate().getTime() + value2.getDate().getTime());
				break;
			case Variant.TIME:
				result.setTime(getTime().getTime() + value2.getTime().getTime());
				break;
			case Variant.FLOAT:
				result.setFloat(getFloat() + value2.getAsFloat());
				break;
			case Variant.DOUBLE:
				result.setDouble(getDouble() + value2.getAsDouble());
				break;
			case Variant.BIGDECIMAL:
				result.setBigDecimal(getBigDecimal().add(value2.getAsBigDecimal()));
				break;
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				result.setVariant(value2);
				break;
			default:
				Diagnostic.println("type:  " + type);
				Diagnostic.fail();
				break;
			}
		}
	}

	/**
	 * Subtracts a Variant value from the value of this Variant, storing the
	 * result in the result parameter.
	 *
	 * @param value2
	 *          The value being subtracted from this Variant.
	 * @param result
	 *          The value being subtracted from this Variant.
	 */
	public void subtract(Variant value2, Variant result) {
		if (value2.isNull() && isNull())
			result.setVariant(this);
		else {
			switch (type) {
			case Variant.BYTE:
			case Variant.SHORT:
			case Variant.INT:
				result.setInt(getAsInt() - value2.getAsInt());
				break;
			case Variant.LONG:
				result.setLong(getLong() - value2.getAsLong());
				break;
			case Variant.FLOAT:
				result.setFloat(getFloat() - value2.getAsFloat());
				break;
			case Variant.DOUBLE:
				result.setDouble(getDouble() - value2.getAsDouble());
				break;
			case Variant.BIGDECIMAL:
				result
				.setBigDecimal(getBigDecimal().subtract(value2.getAsBigDecimal()));
				break;
			case Variant.UNASSIGNED_NULL:
			case Variant.ASSIGNED_NULL:
				result.setVariant(value2);
				break;
			default:
				Diagnostic.fail();
				break;
			}
		}
	}

	/**
	 * Creates a copy of this Variant, returning the copied object.
	 *
	 * @return The copied object.
	 */
	@Override
	public Object clone() {
		Variant value = new Variant(setType);
		value.setVariant(this);
		return value;
	}

	// Serialization support

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.write(type);
		s.write(setType);
		if (value instanceof Serializable)
			s.writeObject(value);
		else {
			if (type != INPUTSTREAM || value == null)
				s.writeObject(null);
			else {
				InputStreamToByteArray temp = new InputStreamToByteArray(
						(InputStream) value);
				s.writeObject(temp);
			}
		}
	}

	private void readObject(ObjectInputStream s)
			throws IOException, ClassNotFoundException {
		type = s.read();
		setType = s.read();
		value = s.readObject();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Variant)
			return equals((Variant) obj);
		return false;
	}

	@Override
	public int hashCode() {
		Object v = getAsObject();
		if (v == null)
			return 0;
		else
			return v.hashCode();
	}

	/**
	 * (SS) Gibt den Nativen Klassen-Typ des angegebenen Varianten-Typs zurück
	 *
	 * @param v
	 *          int Variant-Typ
	 * @return Class
	 */
	public static Class variantToNative(int v) {
		switch (v) {
		case Variant.ASSIGNED_NULL:
			return null;
		case Variant.UNASSIGNED_NULL:
			return null;
		case Variant.BYTE:
			return Byte.class;
		case Variant.SHORT:
			return Short.class;
		case Variant.INT:
			return Integer.class;
		case Variant.LONG:
			return Long.class;

		case Variant.FLOAT:
			return Float.class;
		case Variant.DOUBLE:
			return Double.class;

		case Variant.BIGDECIMAL:
			return Double.class;

		case Variant.BOOLEAN:
			return Boolean.class;

		case Variant.INPUTSTREAM:
			return InputStream.class;

		case Variant.DATE:
			return Date.class;
		case Variant.TIME:
			return Time.class;
		case Variant.TIMESTAMP:
			return Timestamp.class;

		case Variant.STRING:
			return String.class;
		case Variant.BYTE_ARRAY:
			return Byte[].class;
		case Variant.OBJECT:
			return Object.class;

		default:
			return null;
		}
	}

	/**
	 * Gibt anhand einer Nativen Klasse den passenden Variant-Typ zurück
	 *
	 * @param n
	 *          Class
	 * @return int
	 */
	public static int nativeToVariant(Class n) {
		if (n == null)
			return Variant.ASSIGNED_NULL;
		if (n == Integer.class || n == Integer.TYPE)
			return Variant.INT;
		if (n == String.class)
			return Variant.STRING;
		if (n == Double.class || n == Double.TYPE)
			return Variant.DOUBLE;
		if (n == Byte.class || n == Byte.TYPE)
			return Variant.BYTE;
		if (n == Short.class || n == Short.TYPE)
			return Variant.SHORT;
		if (n == Long.class || n == Long.TYPE)
			return Variant.LONG;
		if (n == Float.class || n == Float.TYPE)
			return Variant.FLOAT;
		if (n == Boolean.class || n == Boolean.TYPE)
			return Variant.BOOLEAN;
		if (n == InputStream.class)
			return Variant.INPUTSTREAM;
		if (n == Date.class)
			return Variant.DATE;
		if (n == Time.class)
			return Variant.TIME;
		if (n == Timestamp.class || n == java.util.Date.class)
			return Variant.TIMESTAMP;
		if (n == Byte[].class)
			return Variant.BYTE_ARRAY;
		if (n == Object.class)
			return Variant.OBJECT;
		return Variant.UNASSIGNED_NULL;
	}

	/**
	 * Determines whether a Variant value is equal to this Variant value. If
	 * equals() returns <b>true</b>, the two Variant values are of the same type
	 * and are equal in value. A returned value of <b>false</b> indicates that the
	 * two values differ in value or type.
	 *
	 * @param variant
	 *          The Variant value being compared to the data type and value of
	 *          this Variant.
	 * @return <b>true</b> if the two Variant values are of the same type and are
	 *         equal in value. A returned value of <b>false</b> indicates that the
	 *         two values differ in value or type.
	 */
	public boolean equalsIgnoreCase(Variant variant) {
		if (variant == this)
			return true;

		if (variant == null)
			return false;

		if (type != variant.type) {
			if (type <= NULL_TYPES || variant.type <= NULL_TYPES)
				return false;
			typeProblem(variant.type, type, true, variant);
		}

		switch (type) {
		case ASSIGNED_NULL:
		case UNASSIGNED_NULL:
			return variant.type == type;
		case INT:
		case BYTE:
		case SHORT:
			return getInt() == variant.getInt();
		case BOOLEAN:
			return getBoolean() == variant.getBoolean();
		case FLOAT:
			return getFloat() == variant.getFloat();
		case DOUBLE:
			return getDouble() == variant.getDouble();
		case TIMESTAMP:
			return compareTimestamps(this, variant) == 0;
		case DATE:
			return compareDates(this, variant) == 0;
		case TIME:
			return compareTimes(this, variant) == 0;
		case LONG:
			return ((Long) value).equals(variant.value);
		case BIGDECIMAL:
			return getBigDecimal().equals(variant.getBigDecimal());
		case STRING:
			return getString().equalsIgnoreCase(variant.getString());
		case BYTE_ARRAY:
			return Arrays.equals(getByteArray(), variant.getByteArray());
		case INPUTSTREAM:
			return equals(getInputStream(), variant.getInputStream());
		case OBJECT:
			return getObject().equals(variant.getObject());
		default:
			break; // to make compiler happy
		}
		Diagnostic.fail();
		return false;
	}

	public Number getAsNumber() {
		switch (type) {
		case ASSIGNED_NULL:
		case UNASSIGNED_NULL:
			return null;
		case INT:
			return new Integer(getInt());
		case BYTE:
			return new Byte(getByte());
		case SHORT:
			return new Short(getShort());
		case FLOAT:
			return new Float(getFloat());
		case DOUBLE:
			return new Double(getDouble());
		case LONG:
			return new Long(getLong());
		case BIGDECIMAL:
			return getBigDecimal();
		default:
			typeProblem(type, DOUBLE, true, value);
		}
		return null;
	}

}
