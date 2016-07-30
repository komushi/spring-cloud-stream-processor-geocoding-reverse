package io.pivotal.spring.cloud.stream.processor.geocoding.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by lei_xu on 7/29/16.
 */
//@Document(collection = "mixblocks")
@Document
public class Block {

    @Id
    public String id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    public GeoJsonPolygon geometry;

    @Field("properties")
    public Properties properties;

    public class Properties {

        private String district;

        private String block;

        private String code;

        public void setCode(String code){
            this.code = code;
        }

        public String getCode(){
            return this.code;
        }

        public void setDistrict(String district){
            this.district = district;
        }

        public String getDistrict(){
            return this.district;
        }

        public void setBlock(String block){
            this.block = block;
        }

        public String getBlock(){
            return this.block;
        }
    }
}
