package io;

import java.nio.ByteBuffer;

public final class Attribute {
	
	Attribute(int handle, String attributeName, MESSAGE_TYPE type) {
		
		name = attributeName;
		reference = handle;
		messageType = type;
		data = ByteBuffer.allocate(16);
	}
	
	private boolean newData = false;
	String name;
	MESSAGE_TYPE messageType;
	int reference;
	ByteBuffer data;
	int asInt;
	float asFloat;

	boolean newDataAvailable() {
		return newData;
	}
	
	void newDataAvailable(boolean b) {
		newData = b;
	}
	
	int asInteger() {
		return asInt;
	}
	
	float asFloating() {
		return asFloat;
	}
}