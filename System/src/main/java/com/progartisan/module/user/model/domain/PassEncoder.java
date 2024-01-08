package com.progartisan.module.user.model.domain;

public interface PassEncoder {

	String encode(String raw);

	boolean matches(String raw, String encoded);
}
