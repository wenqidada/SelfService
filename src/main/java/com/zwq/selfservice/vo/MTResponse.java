package com.zwq.selfservice.vo;
import lombok.Data;
import lombok.Builder;

/**
 * 核销券响应实体
 */
@Data
@Builder
public class MTResponse {
    /**
     * 订单ID
     * 示例值: 4962000011402066331
     */
    private String orderId;

    /**
     * 可验证的张数
     * 示例值: 12
     */
    private Integer count;

    /**
     * 验证券码
     * 示例值: 2220761171
     */
    private String receiptCode;

    /**
     * 套餐ID（若验证的券所对应的商品为团购时，该字段必返回），次卡（平台次卡，不是团购次卡）和拼团不返回此参数
     * 示例值: 456574162
     */
    private Long dealId;

    /**
     * 团购ID，团购id与套餐id是一对多的关系（若验证的券所对应的商品为团购时，该字段必返回），次卡（平台次卡，不是团购次卡）和拼团不返回此参数
     * 示例值: 1023738278
     */
    private Long dealGroupId;

    /**
     * 商品名称
     * 示例值: 青少儿体检|今约明检
     */
    private String dealTitle;

    /**
     * 商品售卖价格
     * 示例值: 11
     */
    private Double dealPrice;

    /**
     * 商品市场价
     * 示例值: 22
     */
    private Double dealMarketPrice;

    /**
     * 用户手机号
     * 示例值: 15903679564
     */
    private String mobile;

    /**
     * 业务类型 0:普通团购 203:拼团 205:次卡 217:通兑标品
     * 示例值: 0
     */
    private Integer bizType;

    /**
     * 券过期时间
     * 示例值: 1726415999000
     */
    private Long receiptEndDate;
}