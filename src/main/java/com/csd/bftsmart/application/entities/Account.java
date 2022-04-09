package com.csd.bftsmart.application.entities;

import java.io.Serializable;

public record Account(String id, String userId) implements Serializable {
}
