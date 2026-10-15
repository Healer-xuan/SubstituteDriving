package com.atguigu.daijia.model.form.customer;

import com.atguigu.daijia.common.util.StringOrArrayDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SubmitOrderForm {

    @Schema(description = "乘客id")
    private Long customerId;

    @Schema(description = "起始地点")
    @JsonDeserialize(using = StringOrArrayDeserializer.class)
    private String startLocation;

    @Schema(description = "起始地点经度")
    private BigDecimal startPointLongitude;

    @Schema(description = "起始点伟度")
    private BigDecimal startPointLatitude;

    @Schema(description = "结束地点")
    @JsonDeserialize(using = StringOrArrayDeserializer.class)
    private String endLocation;

    @Schema(description = "结束地点经度")
    private BigDecimal endPointLongitude;

    @Schema(description = "结束地点经度")
    private BigDecimal endPointLatitude;

    @Schema(description = "顾客好处费")
    private BigDecimal favourFee = new BigDecimal(0);

}
