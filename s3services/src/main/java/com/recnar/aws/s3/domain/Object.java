package com.recnar.aws.s3.domain;

import lombok.Builder;
import lombok.Value;

import java.net.URL;

@Value
@Builder
public class Object {
    String name;
    String key;
    URL url;
}
