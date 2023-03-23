package com.reporter.common

//import com.fasterxml.jackson.annotation.JsonInclude
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.databind.MapperFeature
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.databind.SerializationFeature
//
//class Json {
//    class Jackson {
//        companion object {
//            val OBJECT_MAPPER: ObjectMapper
//
//            init {
//                val mapper = ObjectMapper()
//
//                // spring classic configs
//                mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
//                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
//
//                // configs for a GraphQL friendly responses
//                mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
//
//                OBJECT_MAPPER = mapper;
//            }
//        }
//    }
//}