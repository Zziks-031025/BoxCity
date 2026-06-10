package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("merchant_address")
public class MerchantAddress {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private String province;

    private String city;

    private String district;

    private String detail;

    private String zipcode;

    /** 0否 1是 */
    private Integer isDefault;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
