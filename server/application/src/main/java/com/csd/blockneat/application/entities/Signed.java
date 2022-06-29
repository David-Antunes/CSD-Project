package com.csd.blockneat.application.entities;

import java.io.Serializable;
import java.util.Map;

public record Signed<T>(T object, Map<Integer, String> signBase64) implements Serializable {
}
