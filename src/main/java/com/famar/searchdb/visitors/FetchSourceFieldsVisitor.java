package com.famar.searchdb.visitors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.util.BytesRef;

import com.famar.searchdb.FamarDBConstants;

public class FetchSourceFieldsVisitor extends StoredFieldVisitor {

	protected BytesRef source;
	protected UUID uuid;
	protected Map<String, List<Object>> fieldsValues;

	public FetchSourceFieldsVisitor() {
		reset();
    }

	@Override
	public Status needsField(FieldInfo fieldInfo) throws IOException {
		return Status.YES;
	}

	@Override
	public void binaryField(FieldInfo fieldInfo, byte[] value) throws IOException {
		if (FamarDBConstants.FIELDNAME_SOURCE.equals(fieldInfo.name)) {
			source = new BytesRef(value);
		} else {
			addValue(fieldInfo.name, new BytesRef(value));
		}
	}

	@Override
	public void stringField(FieldInfo fieldInfo, byte[] bytes) throws IOException {
		final String value = new String(bytes, StandardCharsets.UTF_8);
		if (FamarDBConstants.FAMARDB_UUID.equals(fieldInfo.name)) {
			uuid = UUID.fromString(value);
		} else {
			addValue(fieldInfo.name, value);
		}
	}

	@Override
	public void intField(FieldInfo fieldInfo, int value) throws IOException {
		addValue(fieldInfo.name, value);
	}

	@Override
	public void longField(FieldInfo fieldInfo, long value) throws IOException {
		addValue(fieldInfo.name, value);
	}

	@Override
	public void floatField(FieldInfo fieldInfo, float value) throws IOException {
		addValue(fieldInfo.name, value);
	}

	@Override
	public void doubleField(FieldInfo fieldInfo, double value) throws IOException {
		addValue(fieldInfo.name, value);
	}

	public BytesRef source() {
		return source;
	}

	public UUID uuid() {
		return uuid;
	}

	public Map<String, List<Object>> fields() {
		return fieldsValues != null ? fieldsValues : Collections.emptyMap();
	}

	public void reset() {
		if (fieldsValues != null)
			fieldsValues.clear();
		source = null;
		uuid = null;
	}

	void addValue(String name, Object value) {
		if (fieldsValues == null) {
			fieldsValues = new HashMap<>();
		}

		List<Object> values = fieldsValues.get(name);
		if (values == null) {
			values = new ArrayList<>(2);
			fieldsValues.put(name, values);
		}
		values.add(value);
	}
}
