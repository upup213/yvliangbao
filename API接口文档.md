# 余量宝 API 接口文档

## 一、概述

### 1.1 基本信息

| 项目 | 说明 |
|------|------|
| 服务地址 | `http://localhost:8080/api` |
| 请求格式 | `application/json` |
| 响应格式 | `application/json` |
| 字符编码 | `UTF-8` |

### 1.2 认证方式

使用 JWT Token 认证，在请求头中携带：

```
Authorization: Bearer {token}
```

### 1.3 通用响应格式

```json
{
    "code": 200,
    "message": "成功",
    "data": {},
    "timestamp": 1772374500000
}
```

### 1.4 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/Token无效 |
| 403 | 无权限 |
| 500 | 服务器内部错误 |

---

## 二、商户接口

### 2.1 商户入驻

**接口地址**：`POST /merchant/register`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantName | String | 是 | 商户名称 |
| merchantType | Integer | 是 | 商户类型：1-餐饮，2-烘焙，3-零售，4-其他 |
| contactName | String | 是 | 联系人姓名 |
| contactPhone | String | 是 | 联系电话 |
| businessLicenseNo | String | 是 | 营业执照号 |
| businessLicenseImg | String | 是 | 营业执照图片URL |
| foodLicenseNo | String | 否 | 食品经营许可证号 |
| foodLicenseImg | String | 否 | 食品经营许可证图片URL |
| legalPersonName | String | 是 | 法人姓名 |
| legalPersonIdCard | String | 是 | 法人身份证号 |
| legalPersonIdCardImg | String | 是 | 法人身份证图片URL |
| bankName | String | 否 | 开户银行 |
| bankAccount | String | 否 | 银行账号 |
| alipayAccount | String | 否 | 支付宝账号 |

**请求示例**：

```json
{
    "merchantName": "美味烘焙坊",
    "merchantType": 2,
    "contactName": "张三",
    "contactPhone": "13900139000",
    "businessLicenseNo": "91110108MA01234567",
    "businessLicenseImg": "https://oss.example.com/license.jpg",
    "legalPersonName": "张三",
    "legalPersonIdCard": "110101199001011234",
    "legalPersonIdCardImg": "https://oss.example.com/idcard.jpg"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "merchantNo": "YL202603012209239488",
        "merchantName": "美味烘焙坊",
        "merchantType": 2,
        "contactName": "张三",
        "contactPhone": "13900139000",
        "status": 0,
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

---

### 2.2 商户登录

**接口地址**：`POST /merchant/login`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| contactPhone | String | 是 | 联系电话（URL参数） |

**请求示例**：

```
POST /merchant/login?contactPhone=13900139000
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "merchantNo": "YL202603012209239488",
        "merchantName": "美味烘焙坊",
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

---

### 2.3 获取商户信息

**接口地址**：`GET /merchant/info`

**请求头**：需要商户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "merchantNo": "YL202603012209239488",
        "merchantName": "美味烘焙坊",
        "merchantType": 2,
        "contactName": "张三",
        "contactPhone": "13900139000",
        "status": 0
    }
}
```

---

### 2.4 获取商户统计数据

**接口地址**：`GET /merchant/stats`

**请求头**：需要商户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "todayOrders": 15,
        "todayRevenue": 258000,
        "todayVerified": 10,
        "todayRefund": 1600,
        "totalOrders": 520,
        "totalRevenue": 8960000,
        "totalRefund": 32000,
        "totalSaved": 1280000,
        "pendingVerify": 5,
        "refunding": 2,
        "storeCount": 3,
        "productCount": 12
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| todayOrders | Integer | 今日订单数（已创建） |
| todayRevenue | Long | 今日营收（分，已核销/已完成订单） |
| todayVerified | Integer | 今日核销数（已完成订单） |
| todayRefund | Long | 今日退款金额（分） |
| totalOrders | Integer | 累计订单数 |
| totalRevenue | Long | 累计营收（分，已支付订单） |
| totalRefund | Long | 累计退款金额（分） |
| totalSaved | Long | 累计节省金额（分） |
| pendingVerify | Integer | 待核销订单数 |
| refunding | Integer | 退款中订单数 |
| storeCount | Integer | 门店数量 |
| productCount | Integer | 在售商品数 |

**说明**：
- `todayRevenue`（今日营收）：统计**已核销完成**（order_status=3）的订单实付金额
- `totalRevenue`（累计营收）：统计**已支付**（pay_status=1）的订单实付金额

---

### 2.5 获取商户营收趋势

**接口地址**：`GET /merchant/revenue/trend`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| days | Integer | 否 | 天数（默认7天） |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "dates": ["02-26", "02-27", "02-28", "02-29", "03-01", "03-02", "03-03"],
        "revenues": [32000, 45000, 28000, 52000, 61000, 48000, 55000],
        "orders": [8, 12, 7, 15, 18, 13, 16],
        "refunds": [0, 0, 0, 0, 0, 1600, 0]
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| dates | List<String> | 日期列表（MM-DD格式） |
| revenues | List<Long> | 每日营收（分，已支付订单） |
| orders | List<Integer> | 每日订单数 |
| refunds | List<Long> | 每日退款金额（分） |

**说明**：
- `revenues` 统计的是**已支付**（pay_status=1）订单的实付金额

---

### 2.6 获取商户历史营收

**接口地址**：`GET /merchant/revenue/history`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 是 | 开始日期（YYYY-MM-DD） |
| endDate | String | 是 | 结束日期（YYYY-MM-DD） |

**请求示例**：

```
GET /merchant/revenue/history?startDate=2026-02-01&endDate=2026-02-28
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": 1258000
}
```

**说明**：
- 统计指定日期范围内**已支付**（pay_status=1）订单的实付金额总和（分）

---

## 三、门店接口

### 3.1 创建门店

**接口地址**：`POST /store/create`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| storeName | String | 是 | 门店名称 |
| storeLogo | String | 否 | 门店Logo |
| contactPhone | String | 是 | 联系电话 |
| province | String | 是 | 省 |
| city | String | 是 | 市 |
| district | String | 是 | 区 |
| detailAddress | String | 是 | 详细地址 |
| longitude | BigDecimal | 是 | 经度 |
| latitude | BigDecimal | 是 | 纬度 |
| businessHoursStart | LocalTime | 是 | 营业开始时间（格式：HH:mm:ss） |
| businessHoursEnd | LocalTime | 是 | 营业结束时间（格式：HH:mm:ss） |
| storeNotice | String | 否 | 门店公告 |
| storeImages | String | 否 | 门店图片JSON数组 |

**请求示例**：

```json
{
    "storeName": "美味烘焙坊-中关村店",
    "contactPhone": "13900139001",
    "province": "北京市",
    "city": "北京市",
    "district": "海淀区",
    "detailAddress": "中关村大街1号",
    "longitude": 116.310003,
    "latitude": 39.995288,
    "businessHoursStart": "08:00:00",
    "businessHoursEnd": "22:00:00"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "merchantId": 1,
        "storeNo": "STR202603012209554689",
        "storeName": "美味烘焙坊-中关村店",
        "contactPhone": "13900139001",
        "province": "北京市",
        "city": "北京市",
        "district": "海淀区",
        "detailAddress": "中关村大街1号",
        "longitude": 116.310003,
        "latitude": 39.995288,
        "businessHoursStart": "08:00:00",
        "businessHoursEnd": "22:00:00",
        "businessStatus": 1,
        "status": 1
    }
}
```

---

### 3.2 门店列表（商户端）

**接口地址**：`GET /store/merchant/list`

**请求头**：需要商户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "storeNo": "STR202603012209554689",
            "storeName": "美味烘焙坊-中关村店",
            "contactPhone": "13900139001",
            "province": "北京市",
            "city": "北京市",
            "district": "海淀区",
            "detailAddress": "中关村大街1号",
            "businessStatus": 1,
            "status": 1
        }
    ]
}
```

---

### 3.3 门店详情

**接口地址**：`GET /store/detail/{id}`

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 门店ID |

**请求示例**：

```
GET /store/detail/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "storeNo": "STR202603012209554689",
        "storeName": "美味烘焙坊-中关村店",
        "contactPhone": "13900139001",
        "province": "北京市",
        "city": "北京市",
        "district": "海淀区",
        "detailAddress": "中关村大街1号",
        "longitude": 116.310003,
        "latitude": 39.995288,
        "businessHoursStart": "08:00:00",
        "businessHoursEnd": "22:00:00",
        "businessStatus": 1,
        "status": 1
    }
}
```

---

### 3.4 周边门店列表（用户端）

**接口地址**：`GET /store/nearby`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| latitude | Double | 是 | 用户纬度 |
| longitude | Double | 是 | 用户经度 |
| radius | Double | 否 | 搜索半径（米），默认3000 |
| sortBy | String | 否 | 排序方式：distance-距离优先，price-价格优先，默认distance |
| storeType | Integer | 否 | 门店类型筛选：1-餐饮，2-烘焙，3-零售，4-其他 |

**请求示例**：

```
GET /store/nearby?latitude=39.995288&longitude=116.310003
GET /store/nearby?latitude=39.995288&longitude=116.310003&radius=5000&sortBy=price&storeType=2
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "storeName": "美味烘焙坊-中关村店",
            "storeLogo": "https://oss.example.com/store/logo.jpg",
            "merchantType": 2,
            "detailAddress": "中关村大街1号",
            "distance": 1200.5,
            "distanceText": "1.2km",
            "productCount": 5,
            "businessStatus": 1,
            "minPrice": 800
        }
    ]
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 门店ID |
| storeName | String | 门店名称 |
| storeLogo | String | 门店Logo URL |
| merchantType | Integer | 商户类型：1-餐饮，2-烘焙，3-零售，4-其他 |
| detailAddress | String | 详细地址 |
| distance | Double | 距离（米） |
| distanceText | String | 距离文本（如"1.2km"） |
| productCount | Integer | 在售商品数量 |
| businessStatus | Integer | 营业状态：1-营业中，0-休息中 |
| minPrice | Integer | 最低商品价格（分） |

**说明**：
- 该接口用于LBS发现功能，根据用户位置返回周边门店
- 距离计算使用Haversine公式，考虑地球曲率
- `sortBy=distance` 按距离从近到远排序
- `sortBy=price` 按最低价格从低到高排序

---

### 3.5 门店详情（用户端）

**接口地址**：`GET /store/explore/{id}`

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 门店ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| latitude | Double | 是 | 用户纬度 |
| longitude | Double | 是 | 用户经度 |

**请求示例**：

```
GET /store/explore/1?latitude=39.995288&longitude=116.310003
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "storeName": "美味烘焙坊-中关村店",
        "storeLogo": "https://oss.example.com/store/logo.jpg",
        "merchantType": 2,
        "province": "北京市",
        "city": "北京市",
        "district": "海淀区",
        "detailAddress": "中关村大街1号",
        "longitude": 116.310003,
        "latitude": 39.995288,
        "contactPhone": "13900139001",
        "businessHoursStart": "08:00:00",
        "businessHoursEnd": "22:00:00",
        "storeNotice": "今日新品上市",
        "storeImages": "[\"https://oss.example.com/store/1.jpg\"]",
        "distance": 1200.5,
        "distanceText": "1.2km",
        "productCount": 5,
        "businessStatus": 1,
        "minPrice": 800
    }
}
```

**说明**：
- 该接口用于用户端探索页查看门店详情
- 返回门店完整信息，包括距离计算

---

### 3.6 更新门店

**接口地址**：`PUT /store/update`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 门店ID |
| storeName | String | 否 | 门店名称 |
| storeLogo | String | 否 | 门店Logo |
| contactPhone | String | 否 | 联系电话 |
| province | String | 否 | 省 |
| city | String | 否 | 市 |
| district | String | 否 | 区 |
| detailAddress | String | 否 | 详细地址 |
| longitude | BigDecimal | 否 | 经度 |
| latitude | BigDecimal | 否 | 纬度 |
| businessHoursStart | LocalTime | 否 | 营业开始时间 |
| businessHoursEnd | LocalTime | 否 | 营业结束时间 |
| storeNotice | String | 否 | 门店公告 |
| storeImages | String | 否 | 门店图片JSON数组 |

**请求示例**：

```json
{
    "id": 1,
    "storeName": "美味烘焙坊-中关村店（升级版）",
    "businessHoursStart": "07:00:00",
    "businessHoursEnd": "23:00:00"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "storeName": "美味烘焙坊-中关村店（升级版）",
        "businessHoursStart": "07:00:00",
        "businessHoursEnd": "23:00:00"
    }
}
```

---

## 四、商品接口

### 4.1 发布盲盒

**接口地址**：`POST /product/create`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| storeId | Long | 是 | 门店ID |
| productName | String | 是 | 商品名称 |
| productDesc | String | 否 | 商品描述 |
| productImages | String | 否 | 商品图片JSON数组 |
| categoryId | Long | 否 | 分类ID |
| originalPrice | BigDecimal | 是 | 原价（分） |
| salePrice | BigDecimal | 是 | 清仓价（分） |
| totalStock | Integer | 是 | 总库存 |
| pickupTimeStart | LocalTime | 是 | 取餐开始时间（格式：HH:mm:ss） |
| pickupTimeEnd | LocalTime | 是 | 取餐结束时间（格式：HH:mm:ss） |
| purchaseLimit | Integer | 否 | 单人限购数量，默认1 |

**请求示例**：

```json
{
    "storeId": 1,
    "productName": "今日鲜奶吐司盲盒",
    "productDesc": "包含当日现做吐司，随机搭配，超值优惠",
    "originalPrice": 2000,
    "salePrice": 800,
    "totalStock": 20,
    "pickupTimeStart": "18:00:00",
    "pickupTimeEnd": "21:00:00",
    "purchaseLimit": 2
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "storeId": 1,
        "productNo": "P202603012210075296",
        "productName": "今日鲜奶吐司盲盒",
        "productDesc": "包含当日现做吐司，随机搭配，超值优惠",
        "originalPrice": 2000.00,
        "salePrice": 800.00,
        "discountRate": 40.00,
        "pickupTimeStart": "18:00:00",
        "pickupTimeEnd": "21:00:00",
        "purchaseLimit": 2,
        "status": 1,
        "soldCount": 0
    }
}
```

**说明**：
- 价格单位为"分"，如 `2000` = 20.00元
- `discountRate`（折扣率）由系统自动计算：`salePrice / originalPrice * 100`

---

### 4.2 更新商品

**接口地址**：`PUT /product/update`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |
| storeId | Long | 否 | 门店ID |
| productName | String | 否 | 商品名称 |
| productDesc | String | 否 | 商品描述 |
| productImages | String | 否 | 商品图片JSON数组 |
| categoryId | Long | 否 | 分类ID |
| originalPrice | BigDecimal | 否 | 原价（分） |
| salePrice | BigDecimal | 否 | 清仓价（分） |
| totalStock | Integer | 否 | 总库存 |
| pickupTimeStart | LocalTime | 否 | 取餐开始时间（格式：HH:mm:ss） |
| pickupTimeEnd | LocalTime | 否 | 取餐结束时间（格式：HH:mm:ss） |
| purchaseLimit | Integer | 否 | 单人限购数量 |

**请求示例**：

```json
{
    "id": 1,
    "productName": "今日鲜奶吐司盲盒（升级版）",
    "productDesc": "包含当日现做吐司，随机搭配，超值优惠",
    "productImages": "[\"https://oss.example.com/product/xxx.jpg\"]",
    "originalPrice": 2500,
    "salePrice": 1000,
    "totalStock": 30,
    "pickupTimeStart": "17:00:00",
    "pickupTimeEnd": "22:00:00",
    "purchaseLimit": 3
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "storeId": 1,
        "productNo": "P202603012210075296",
        "productName": "今日鲜奶吐司盲盒（升级版）",
        "productDesc": "包含当日现做吐司，随机搭配，超值优惠",
        "productImages": "[\"https://oss.example.com/product/xxx.jpg\"]",
        "originalPrice": 2500.00,
        "salePrice": 1000.00,
        "discountRate": 40.00,
        "pickupTimeStart": "17:00:00",
        "pickupTimeEnd": "22:00:00",
        "purchaseLimit": 3,
        "status": 1,
        "soldCount": 5
    }
}
```

**说明**：
- 只需传入需要更新的字段，不传的字段保持不变
- 更新库存时会同步更新可用库存
- 折扣率会根据原价和清仓价自动重新计算

---

### 4.3 商品列表（商户端）

**接口地址**：`GET /product/merchant/list`

**请求头**：需要商户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "storeId": 1,
            "productNo": "P202603012210075296",
            "productName": "今日鲜奶吐司盲盒",
            "originalPrice": 2000.00,
            "salePrice": 800.00,
            "discountRate": 40.00,
            "status": 1,
            "soldCount": 5
        }
    ]
}
```

---

### 4.4 商品列表（用户端）

**接口地址**：`GET /product/list`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| storeId | Long | 否 | 门店ID，不传则查询所有门店 |
| status | Integer | 否 | 商品状态，不传默认查询上架商品 |

**商品状态说明**：

| 状态值 | 说明 |
|--------|------|
| 0 | 已下架 |
| 1 | 在售 |
| 2 | 已售罄 |

**请求示例**：

```
GET /product/list
GET /product/list?storeId=1
GET /product/list?storeId=1&status=1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "storeId": 1,
            "productNo": "P202603012210075296",
            "productName": "今日鲜奶吐司盲盒",
            "productDesc": "包含当日现做吐司，随机搭配，超值优惠",
            "originalPrice": 2000.00,
            "salePrice": 800.00,
            "discountRate": 40.00,
            "pickupTimeStart": "18:00:00",
            "pickupTimeEnd": "21:00:00",
            "purchaseLimit": 2,
            "status": 1
        }
    ]
}
```

---

### 4.5 商品详情

**接口地址**：`GET /product/detail/{id}`

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

**请求示例**：

```
GET /product/detail/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "storeId": 1,
        "productNo": "P202603012210075296",
        "productName": "今日鲜奶吐司盲盒",
        "productDesc": "包含当日现做吐司，随机搭配，超值优惠",
        "originalPrice": 2000.00,
        "salePrice": 800.00,
        "discountRate": 40.00,
        "pickupTimeStart": "18:00:00",
        "pickupTimeEnd": "21:00:00",
        "purchaseLimit": 2,
        "status": 1,
        "soldCount": 5,
        "createTime": "2026-03-01T22:10:08"
    }
}
```

---

### 4.6 上架商品

**接口地址**：`POST /product/online/{id}`

**请求头**：需要商户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

**请求示例**：

```
POST /product/online/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

### 4.7 下架商品

**接口地址**：`POST /product/offline/{id}`

**请求头**：需要商户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 商品ID |

**请求示例**：

```
POST /product/offline/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

## 五、用户接口

### 5.1 微信授权登录

**接口地址**：`POST /user/login/wechat`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| code | String | 是 | 微信小程序登录凭证 |
| nickname | String | 否 | 用户昵称 |
| avatar | String | 否 | 用户头像URL |

**请求示例**：

```json
{
    "code": "081xKxll2CWRxR4FK3nl2XLQll2xKxl7",
    "nickname": "测试用户",
    "avatar": "https://example.com/avatar.jpg"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "openid": "oXXXX-xxxxxxxxxxxxxxxx",
        "nickname": "测试用户",
        "avatar": "https://example.com/avatar.jpg",
        "balance": 0.00,
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
}
```

**说明**：
- 开发环境下如未配置微信小程序，会使用模拟登录，openid 格式为 `mock_openid_{code}`

---

### 5.2 获取用户信息

**接口地址**：`GET /user/info`

**请求头**：需要用户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "openid": "oXXXX-xxxxxxxxxxxxxxxx",
        "phone": "13800138000",
        "nickname": "测试用户",
        "avatar": "https://example.com/avatar.jpg",
        "balance": 10000.00,
        "savedFoodWeight": 5.50,
        "carbonReduction": 2.20
    }
}
```

---

### 5.3 绑定手机号

**接口地址**：`POST /user/bindPhone`

**请求头**：需要用户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | String | 是 | 手机号（URL参数） |

**请求示例**：

```
POST /user/bindPhone?phone=13800138000
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "phone": "13800138000",
        "nickname": "测试用户"
    }
}
```

---

### 5.4 获取用户统计数据

**接口地址**：`GET /user/stats`

**请求头**：需要用户Token

**功能说明**：获取用户个人主页数据看板的完整统计数据

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "unpaidCount": 0,
        "waitingCount": 2,
        "completedCount": 10,
        "cancelledCount": 1,
        "refundingCount": 0,
        "totalSpent": 250.00,
        "totalSaved": 150.00,
        "monthSpent": 80.00,
        "monthSaved": 45.00,
        "savedFoodWeight": 5.00,
        "carbonReduction": 2.00,
        "ecoActions": 10,
        "balance": 150.00,
        "totalRecharged": 200.00
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| unpaidCount | Integer | 待支付订单数 |
| waitingCount | Integer | 待取货订单数（已支付+待取餐） |
| completedCount | Integer | 已完成订单数 |
| cancelledCount | Integer | 已取消订单数 |
| refundingCount | Integer | 退款中订单数 |
| totalSpent | BigDecimal | 累计消费金额（元） |
| totalSaved | BigDecimal | 累计节省金额（元） |
| monthSpent | BigDecimal | 本月消费金额（元） |
| monthSaved | BigDecimal | 本月节省金额（元） |
| savedFoodWeight | BigDecimal | 拯救食物重量（kg） |
| carbonReduction | BigDecimal | 减碳量（kg CO₂） |
| ecoActions | Integer | 环保行动次数 |
| balance | BigDecimal | 账户余额（元） |
| totalRecharged | BigDecimal | 累计充值金额（元） |

---

### 5.5 余额充值

**接口地址**：`POST /balance/recharge`

**请求头**：需要用户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| amount | BigDecimal | 是 | 充值金额（元） |
| payMethod | Integer | 否 | 充值方式：1-微信支付，2-模拟充值（默认2） |

**请求示例**：

```json
{
    "amount": 100.00,
    "payMethod": 2
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "logNo": "BL20260304153000123456",
        "amount": 100.00,
        "balance": 150.00,
        "payMethodDesc": "模拟充值"
    }
}
```

---

### 5.5 获取余额变动记录

**接口地址**：`GET /balance/logs`

**请求头**：需要用户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页条数（默认20，最大50） |

**请求示例**：

```
GET /balance/logs?page=1&size=20
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "logNo": "BL20260304153000123456",
            "changeType": 1,
            "changeTypeDesc": "充值",
            "changeAmount": 100.00,
            "afterBalance": 150.00,
            "remark": "模拟充值",
            "createTime": "2026-03-04T15:30:00"
        },
        {
            "logNo": "BL20260304120000123455",
            "changeType": 2,
            "changeTypeDesc": "消费",
            "changeAmount": -25.00,
            "afterBalance": 50.00,
            "remark": "订单消费",
            "createTime": "2026-03-04T12:00:00"
        }
    ]
}
```

**变动类型说明**：

| changeType | 说明 |
|------------|------|
| 1 | 充值 |
| 2 | 消费 |
| 3 | 退款 |
| 4 | 系统赠送 |

---

## 六、订单接口

### 6.1 创建订单（下单）

**接口地址**：`POST /order/create`

**请求头**：需要用户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品ID |
| quantity | Integer | 是 | 购买数量 |

**请求示例**：

```json
{
    "productId": 1,
    "quantity": 2
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "userId": 1,
        "merchantId": 1,
        "storeId": 1,
        "productId": 1,
        "productName": "今日鲜奶吐司盲盒",
        "quantity": 2,
        "originalPrice": 2000.00,
        "salePrice": 800.00,
        "totalAmount": 1600.00,
        "payAmount": 1600.00,
        "pickupTimeStart": "18:00:00",
        "pickupTimeEnd": "21:00:00",
        "pickupCode": "034464",
        "orderStatus": 0,
        "payStatus": 0,
        "expireTime": "2026-03-01T22:29:55"
    }
}
```

**说明**：
- 订单创建后状态为"待支付"
- `expireTime` 为支付超时时间（默认15分钟）
- `pickupCode` 为6位数字提货码，支付后用于核销

---

### 6.2 支付订单

**接口地址**：`POST /order/pay/{orderNo}`

**请求头**：需要用户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求示例**：

```
POST /order/pay/YL20260301221455514386
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "orderStatus": 1,
        "payStatus": 1,
        "payTime": "2026-03-01T22:15:09",
        "pickupCode": "034464"
    }
}
```

---

### 6.3 我的订单

**接口地址**：`GET /order/my`

**请求头**：需要用户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 订单状态筛选（-1:全部, 0:待支付, -2:待取货, 3:已完成, 4:已取消, 6:退款中） |
| keyword | String | 否 | 搜索关键词（支持订单号、店铺名或商品名模糊匹配） |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认20） |

**状态筛选说明**：

| status值 | 说明 |
|----------|------|
| -1 | 全部订单 |
| 0 | 待支付 |
| -2 | 待取货（包含已支付1 + 待取餐2） |
| 3 | 已完成 |
| 4 | 已取消 |
| 6 | 退款中 |

**请求示例**：

```
GET /order/my
GET /order/my?status=0
GET /order/my?status=-2
GET /order/my?keyword=YL202603
GET /order/my?keyword=好利来
GET /order/my?keyword=吐司盲盒
GET /order/my?status=3&page=1&size=10
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "orderNo": "YL20260301221455514386",
                "storeName": "好利来蛋糕店",
                "productName": "今日鲜奶吐司盲盒",
                "productImages": "[\"https://oss.example.com/product/xxx.jpg\"]",
                "quantity": 2,
                "totalAmount": 1600.00,
                "payAmount": 1600.00,
                "orderStatus": 1,
                "payStatus": 1,
                "pickupCode": "034464",
                "createTime": "2026-03-01T22:14:55"
            }
        ],
        "total": 50,
        "page": 1,
        "size": 20
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 订单列表 |
| total | Integer | 总记录数 |
| page | Integer | 当前页码 |
| size | Integer | 每页大小 |
| storeName | String | 门店名称（快照） |

---

### 6.4 取消订单

**接口地址**：`POST /order/cancel/{orderNo}`

**请求头**：需要用户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求示例**：

```
POST /order/cancel/YL20260301221455514386
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "orderStatus": 4,
        "cancelTime": "2026-03-01T22:15:46",
        "cancelReason": "用户取消"
    }
}
```

**说明**：
- 只能取消"待支付"状态的订单
- 取消后会自动恢复库存

---

### 6.5 核销订单

**接口地址**：`POST /order/verify/{pickupCode}`

**请求头**：需要商户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pickupCode | String | 是 | 6位数字提货码 |

**请求示例**：

```
POST /order/verify/034464
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "productName": "今日鲜奶吐司盲盒",
        "quantity": 2,
        "orderStatus": 3,
        "finishTime": "2026-03-01T22:15:22"
    }
}
```

**说明**：
- 核销后订单状态变为"已完成"
- 支持核销"已支付(1)"或"待取餐(2)"状态的订单
- 商户端支持以下核销方式：
  - **手动输入**：直接输入6位取餐码
  - **摄像头扫码**：打开摄像头扫描用户手机上的二维码
  - **图片识别**：上传二维码截图，自动解析取餐码

---

### 6.6 订单详情

**接口地址**：`GET /order/detail/{orderNo}`

**请求头**：需要用户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求示例**：

```
GET /order/detail/YL20260301221455514386
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "userId": 1,
        "merchantId": 1,
        "storeId": 1,
        "productId": 1,
        "productName": "今日鲜奶吐司盲盒",
        "quantity": 2,
        "originalPrice": 2000.00,
        "salePrice": 800.00,
        "totalAmount": 1600.00,
        "payAmount": 1600.00,
        "pickupTimeStart": "18:00:00",
        "pickupTimeEnd": "21:00:00",
        "pickupCode": "034464",
        "orderStatus": 1,
        "payStatus": 1,
        "refundApplyTime": null,
        "refundTime": null,
        "refundReason": null,
        "createTime": "2026-03-01T22:14:55",
        "payTime": "2026-03-01T22:15:09"
    }
}
```

**取餐码二维码格式说明**：

用户端在订单详情页会生成取餐二维码，二维码内容格式为：

```
PICKUP:{6位取餐码}
```

例如：`PICKUP:034464`

商户端扫描或上传二维码图片后，系统会自动解析提取取餐码进行核销。

---

### 6.7 备餐完成

**接口地址**：`POST /order/ready/{orderNo}`

**请求头**：需要商户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求示例**：

```
POST /order/ready/YL20260301221455514386
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "orderStatus": 2,
        "readyTime": "2026-03-01T22:20:00"
    }
}
```

**说明**：
- 商户备餐完成后调用，订单状态从"已支付(1)"变为"待取餐(2)"
- 使用原子更新保证并发安全，避免与用户退款操作冲突

---

### 6.8 申请退款

**接口地址**：`POST /order/refund/{orderNo}`

**请求头**：需要用户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reason | String | 否 | 退款原因（URL参数，默认"用户申请退款"） |

**请求示例**：

```
POST /order/refund/YL20260301221455514386
POST /order/refund/YL20260301221455514386?reason=不想要了
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "orderStatus": 6,
        "refundReason": "不想要了"
    }
}
```

**说明**：
- 只能申请退款"已支付(1)"或"待取餐(2)"状态的订单
- 申请退款后订单状态变为"退款中(6)"，等待商户审核
- 系统会记录退款申请时间（`refundApplyTime`），用于超时自动退款
- 使用原子更新保证并发安全，避免与商户核销操作冲突
- **超时自动退款**：如果商户24小时内未处理，系统将自动批准退款

---

### 6.9 商户同意退款

**接口地址**：`POST /order/refund/approve/{orderNo}`

**请求头**：需要商户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求示例**：

```
POST /order/refund/approve/YL20260301221455514386
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "orderStatus": 5,
        "payStatus": 3,
        "refundTime": "2026-03-01T22:25:00"
    }
}
```

**说明**：
- 只能审核"退款中(6)"状态的订单
- 同意退款后订单状态变为"已退款(5)"
- 退款成功后自动恢复库存

---

### 6.10 商户拒绝退款

**接口地址**：`POST /order/refund/reject/{orderNo}`

**请求头**：需要商户Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reason | String | 否 | 拒绝原因（URL参数，默认"不符合退款条件"） |

**请求示例**：

```
POST /order/refund/reject/YL20260301221455514386?reason=商品已核销
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "orderNo": "YL20260301221455514386",
        "orderStatus": 1
    }
}
```

**说明**：
- 只能审核"退款中(6)"状态的订单
- 拒绝退款后订单状态恢复为"已支付(1)"
- 用户可重新申请退款或继续等待取货

---

### 6.11 商户订单列表

**接口地址**：`GET /order/merchant/list`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 订单状态筛选 |
| orderNo | String | 否 | 订单号搜索 |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |

**请求示例**：

```
GET /order/merchant/list
GET /order/merchant/list?status=6
GET /order/merchant/list?orderNo=YL202603
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "orderNo": "YL20260301221455514386",
                "productName": "今日鲜奶吐司盲盒",
                "quantity": 2,
                "totalAmount": 1600.00,
                "pickupCode": "034464",
                "orderStatus": 6,
                "refundReason": "不想要了",
                "createTime": "2026-03-01T22:14:55"
            }
        ],
        "total": 50,
        "page": 1,
        "size": 10
    }
}
```

---

## 七、结算接口

### 7.1 获取结算规则说明

**接口地址**：`GET /merchant/settlement/rules`

**请求头**：需要商户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "ruleName": "T+1结算",
            "ruleDesc": "订单完成后1个工作日内结算到账户",
            "cycleDays": 1,
            "feeRate": 0.00
        },
        {
            "id": 2,
            "ruleName": "T+7结算",
            "ruleDesc": "订单完成后7个工作日内结算到账户",
            "cycleDays": 7,
            "feeRate": 0.00
        }
    ]
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 规则ID |
| ruleName | String | 规则名称 |
| ruleDesc | String | 规则描述 |
| cycleDays | Integer | 结算周期（天） |
| feeRate | BigDecimal | 服务费率（%） |

---

### 7.2 获取结算记录

**接口地址**：`GET /merchant/settlement/records`

**请求头**：需要商户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "settleNo": "ST2026030512000001",
            "merchantId": 1,
            "settleAmount": 150000,
            "feeAmount": 0,
            "actualAmount": 150000,
            "orderCount": 25,
            "status": 1,
            "statusDesc": "已结算",
            "bankName": "中国工商银行",
            "bankAccount": "6222***********1234",
            "remark": "T+1结算",
            "settleTime": "2026-03-05T12:00:00",
            "createTime": "2026-03-04T12:00:00"
        }
    ]
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 结算记录ID |
| settleNo | String | 结算单号 |
| merchantId | Long | 商户ID |
| settleAmount | Long | 结算金额（分） |
| feeAmount | Long | 服务费（分） |
| actualAmount | Long | 实际到账金额（分） |
| orderCount | Integer | 结算订单数 |
| status | Integer | 结算状态：0-待结算，1-已结算，2-已拒绝 |
| statusDesc | String | 状态描述 |
| bankName | String | 开户银行 |
| bankAccount | String | 银行账号（脱敏） |
| remark | String | 备注 |
| settleTime | LocalDateTime | 结算时间 |
| createTime | LocalDateTime | 创建时间 |

---

### 7.3 获取资金流水

**接口地址**：`GET /merchant/settlement/flows`

**请求头**：需要商户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| flowType | Integer | 否 | 流水类型：1-收入，2-退款，3-提现，4-服务费扣减，不传则查全部 |

**请求示例**：

```
GET /merchant/settlement/flows
GET /merchant/settlement/flows?flowType=1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "flowNo": "CF2026030512000001",
            "merchantId": 1,
            "flowType": 1,
            "flowTypeDesc": "收入",
            "amount": 10000,
            "beforeBalance": 50000,
            "afterBalance": 60000,
            "remark": "订单收入-YL20260301221455514386",
            "orderNo": "YL20260301221455514386",
            "createTime": "2026-03-05T12:00:00"
        },
        {
            "id": 2,
            "flowNo": "CF2026030515000002",
            "merchantId": 1,
            "flowType": 2,
            "flowTypeDesc": "退款",
            "amount": -5000,
            "beforeBalance": 60000,
            "afterBalance": 55000,
            "remark": "订单退款",
            "orderNo": "YL20260301221455514385",
            "createTime": "2026-03-05T15:00:00"
        }
    ]
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 流水ID |
| flowNo | String | 流水单号 |
| merchantId | Long | 商户ID |
| flowType | Integer | 流水类型：1-收入，2-退款，3-提现，4-服务费扣减 |
| flowTypeDesc | String | 类型描述 |
| amount | Long | 变动金额（分，正数增加，负数减少） |
| beforeBalance | Long | 变动前余额（分） |
| afterBalance | Long | 变动后余额（分） |
| remark | String | 备注 |
| orderNo | String | 关联订单号 |
| createTime | LocalDateTime | 创建时间 |

---

### 7.4 获取商户资金概览

**接口地址**：`GET /merchant/settlement/overview`

**请求头**：需要商户Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "pendingAmount": 2500.00,
        "balance": 15000.00
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| pendingAmount | BigDecimal | 待结算金额（元） |
| balance | BigDecimal | 当前账户余额（元） |

**说明**：
- `pendingAmount`：可结算但尚未结算的金额
- `balance`：商户当前总账户余额

---

## 八、平台管理接口

### 8.1 管理员登录

**接口地址**：`POST /admin/login`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 管理员账号 |
| password | String | 是 | 密码 |

**请求示例**：

```json
{
    "username": "admin",
    "password": "123456"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "admin": {
            "id": 1,
            "username": "admin",
            "realName": "系统管理员",
            "phone": "13800138000",
            "email": "admin@example.com",
            "status": 1
        }
    }
}
```

---

### 7.2 获取管理员信息

**接口地址**：`GET /admin/info`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "phone": "13800138000",
        "email": "admin@example.com",
        "status": 1
    }
}
```

---

### 7.3 轮播图列表

**接口地址**：`GET /admin/banner/list`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "title": "首页轮播图1",
            "imageUrl": "https://oss.example.com/banner/xxx.jpg",
            "linkType": 0,
            "linkUrl": "",
            "linkId": null,
            "sort": 1,
            "status": 1,
            "createTime": "2026-03-01T10:00:00"
        }
    ]
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 轮播图ID |
| title | String | 标题 |
| imageUrl | String | 图片URL |
| linkType | Integer | 跳转类型：0-无跳转，1-商品详情，2-门店详情，3-外部链接 |
| linkUrl | String | 跳转链接 |
| linkId | String | 关联ID（商品ID或门店ID） |
| sort | Integer | 排序（数字越小越靠前） |
| status | Integer | 状态：0-禁用，1-启用 |

---

### 7.4 添加轮播图

**接口地址**：`POST /admin/banner/create`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 是 | 标题 |
| imageUrl | String | 是 | 图片URL |
| linkType | Integer | 否 | 跳转类型：0-无跳转，1-商品详情，2-门店详情，3-外部链接 |
| linkUrl | String | 否 | 跳转链接 |
| linkId | String | 否 | 关联ID |
| sort | Integer | 否 | 排序（默认0） |
| status | Integer | 否 | 状态：0-禁用，1-启用（默认1） |

**请求示例**：

```json
{
    "title": "新品上市",
    "imageUrl": "https://oss.example.com/banner/new-product.jpg",
    "linkType": 1,
    "linkId": "123",
    "sort": 1,
    "status": 1
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 2,
        "title": "新品上市",
        "imageUrl": "https://oss.example.com/banner/new-product.jpg",
        "linkType": 1,
        "linkUrl": "/pages/product/detail?id=123",
        "linkId": "123",
        "sort": 1,
        "status": 1
    }
}
```

---

### 7.5 更新轮播图

**接口地址**：`PUT /admin/banner/update`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 轮播图ID |
| title | String | 否 | 标题 |
| imageUrl | String | 否 | 图片URL |
| linkType | Integer | 否 | 跳转类型 |
| linkUrl | String | 否 | 跳转链接 |
| linkId | String | 否 | 关联ID |
| sort | Integer | 否 | 排序 |
| status | Integer | 否 | 状态 |

**请求示例**：

```json
{
    "id": 1,
    "title": "限时特惠",
    "sort": 0,
    "status": 1
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "title": "限时特惠",
        "sort": 0,
        "status": 1
    }
}
```

---

### 7.6 删除轮播图

**接口地址**：`DELETE /admin/banner/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 轮播图ID |

**请求示例**：

```
DELETE /admin/banner/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

### 7.7 更新轮播图状态

**接口地址**：`PUT /admin/banner/status/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 轮播图ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 状态：0-禁用，1-启用 |

**请求示例**：

```
PUT /admin/banner/status/1?status=0
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

### 7.8 商户列表

**接口地址**：`GET /admin/merchant/list`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantName | String | 否 | 商户名称（模糊搜索） |
| contactPhone | String | 否 | 联系人手机号 |
| status | Integer | 否 | 商户状态：0-待完善资料，1-待审核，2-正常，3-已驳回，4-已禁用 |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |

**请求示例**：

```
GET /admin/merchant/list
GET /admin/merchant/list?status=2
GET /admin/merchant/list?merchantName=烘焙
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "records": [
            {
                "id": 1,
                "merchantNo": "YL202603012209239488",
                "merchantName": "美味烘焙坊",
                "merchantType": 2,
                "merchantTypeDesc": "烘焙",
                "contactName": "张三",
                "contactPhone": "13900139000",
                "status": 2,
                "statusDesc": "正常",
                "createTime": "2026-03-01T22:09:23"
            }
        ],
        "total": 50,
        "size": 10,
        "current": 1,
        "pages": 5
    }
}
```

---

### 7.9 审核通过商户

**接口地址**：`POST /admin/merchant/audit/approve/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| remark | String | 否 | 备注 |

**请求示例**：

```
POST /admin/merchant/audit/approve/1
POST /admin/merchant/audit/approve/1?remark=资质齐全，通过审核
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 7.10 拒绝商户入驻

**接口地址**：`POST /admin/merchant/audit/reject/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reason | String | 是 | 拒绝原因 |

**请求示例**：

```
POST /admin/merchant/audit/reject/1?reason=资质不全
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 7.11 禁用商户

**接口地址**：`POST /admin/merchant/disable/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reason | String | 是 | 封禁原因 |

**请求示例**：

```
POST /admin/merchant/disable/1?reason=违规经营
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 7.12 启用商户

**接口地址**：`POST /admin/merchant/enable/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**请求示例**：

```
POST /admin/merchant/enable/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 7.13 用户管理列表

**接口地址**：`GET /admin/user/list`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 状态筛选：0-禁用，1-正常 |
| keyword | String | 否 | 关键词搜索（昵称/手机号） |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |

**请求示例**：

```
GET /admin/user/list
GET /admin/user/list?status=1
GET /admin/user/list?keyword=138
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "openid": "oXXXX-xxxxxxxxxxxxxxxx",
                "phone": "13800138000",
                "nickname": "测试用户",
                "avatar": "https://example.com/avatar.jpg",
                "balance": 10000.00,
                "status": 1,
                "createTime": "2026-03-01T10:00:00"
            }
        ],
        "total": 100,
        "page": 1,
        "size": 10
    }
}
```

---

### 7.14 禁用用户

**接口地址**：`POST /admin/user/disable/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求示例**：

```
POST /admin/user/disable/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

### 7.15 启用用户

**接口地址**：`POST /admin/user/enable/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求示例**：

```
POST /admin/user/enable/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

### 7.16 数据统计概览

**接口地址**：`GET /admin/statistics/overview`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "totalMerchants": 50,
        "totalUsers": 1000,
        "totalStores": 80,
        "totalOrders": 5000,
        "totalAmount": 150000.00,
        "todayOrders": 100,
        "todayAmount": 3000.00,
        "todayNewUsers": 15,
        "todayNewMerchants": 3,
        "activeUsers": 350,
        "activeMerchants": 35
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| totalMerchants | Long | 总商户数 |
| totalUsers | Long | 总用户数 |
| totalStores | Long | 总门店数 |
| totalOrders | Long | 总订单数 |
| totalAmount | BigDecimal | 交易总额（分） |
| todayOrders | Long | 今日订单数 |
| todayAmount | BigDecimal | 今日交易额（分） |
| todayNewUsers | Long | 今日新增用户 |
| todayNewMerchants | Long | 今日新增商户 |
| activeUsers | Long | 活跃用户（30天内有订单） |
| activeMerchants | Long | 活跃商户（30天内有订单） |

---

### 7.17 数据趋势统计

**接口地址**：`GET /admin/statistics/trend`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| days | Integer | 否 | 统计天数（默认7天） |

**请求示例**：

```
GET /admin/statistics/trend?days=7
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "date": "2026-03-01",
            "orderCount": 50,
            "orderAmount": 1500.00,
            "newUsers": 10
        },
        {
            "date": "2026-03-02",
            "orderCount": 60,
            "orderAmount": 1800.00,
            "newUsers": 15
        }
    ]
}
```

---

### 7.18 用户统计数据

**接口地址**：`GET /admin/statistics/users`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "totalUsers": 1000,
        "todayNew": 15,
        "monthNew": 200,
        "activeUsers": 350,
        "totalSavedFoodWeight": 5000.00,
        "totalCarbonReduction": 2000.00
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| totalUsers | Long | 总用户数 |
| todayNew | Long | 今日新增用户 |
| monthNew | Long | 本月新增用户 |
| activeUsers | Long | 活跃用户（30天内有订单） |
| totalSavedFoodWeight | BigDecimal | 累计节省食物重量（kg） |
| totalCarbonReduction | BigDecimal | 累计减碳量（kg CO₂） |

---

### 7.19 商户统计数据

**接口地址**：`GET /admin/statistics/merchants`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "totalMerchants": 50,
        "pending": 5,
        "normal": 40,
        "rejected": 3,
        "banned": 2,
        "totalStores": 80,
        "openStores": 65,
        "activeMerchants": 35
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| totalMerchants | Long | 总商户数 |
| pending | Long | 待审核 |
| normal | Long | 正常运营 |
| rejected | Long | 已驳回 |
| banned | Long | 已禁用 |
| totalStores | Long | 总门店数 |
| openStores | Long | 营业中门店 |
| activeMerchants | Long | 活跃商户（30天内有订单） |

---

### 7.20 区域统计数据

**接口地址**：`GET /admin/statistics/regions`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "city": "北京市",
            "storeCount": 30,
            "orderCount": 1500,
            "totalAmount": 300000
        },
        {
            "city": "上海市",
            "storeCount": 25,
            "orderCount": 1200,
            "totalAmount": 250000
        }
    ]
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| city | String | 城市名称 |
| storeCount | Long | 门店数量 |
| orderCount | Long | 订单数量 |
| totalAmount | BigDecimal | 交易金额（分） |

---

### 7.21 GMV 统计数据

**接口地址**：`GET /admin/statistics/gmv`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "todayGmv": 15000.00,
        "monthGmv": 150000.00,
        "totalGmv": 500000.00,
        "todayOrders": 150,
        "monthOrders": 1500,
        "totalOrders": 5000
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| todayGmv | BigDecimal | 今日 GMV（元） |
| monthGmv | BigDecimal | 本月 GMV（元） |
| totalGmv | BigDecimal | 累计 GMV（元） |
| todayOrders | Long | 今日订单数 |
| monthOrders | Long | 本月订单数 |
| totalOrders | Long | 累计订单数 |

---

## 八、商户审核管理接口

### 8.1 商户审核列表

**接口地址**：`GET /admin/merchant/audit/list`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantName | String | 否 | 商户名称（模糊搜索） |
| contactPhone | String | 否 | 联系人手机号 |
| status | Integer | 否 | 商户状态：0-待审核，1-正常，2-已驳回，3-已禁用 |
| startDate | String | 否 | 开始日期（创建时间） |
| endDate | String | 否 | 结束日期（创建时间） |
| pageNum | Integer | 否 | 当前页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "records": [
            {
                "id": 1,
                "merchantNo": "M202603050001",
                "merchantName": "测试餐饮店",
                "merchantType": 1,
                "merchantTypeDesc": "餐饮",
                "contactName": "张三",
                "contactPhone": "13800138000",
                "status": 0,
                "statusDesc": "待审核",
                "createTime": "2026-03-05T10:00:00"
            }
        ],
        "total": 10,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 商户ID |
| merchantNo | String | 商户编号 |
| merchantName | String | 商户名称 |
| merchantType | Integer | 商户类型：1-餐饮，2-烘焙，3-零售，4-其他 |
| contactName | String | 联系人姓名 |
| contactPhone | String | 联系人电话 |
| status | Integer | 状态：0-待审核，1-正常，2-已驳回，3-已禁用 |
| statusDesc | String | 状态描述 |
| createTime | LocalDateTime | 创建时间 |

---

### 8.2 商户资质详情

**接口地址**：`GET /admin/merchant/audit/detail/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "merchantNo": "M202603050001",
        "merchantName": "测试餐饮店",
        "merchantType": 1,
        "merchantTypeDesc": "餐饮",
        "contactName": "张三",
        "contactPhone": "13800138000",
        "businessLicenseNo": "91110000XXXXXXXX",
        "businessLicenseImg": "https://xxx.com/license.jpg",
        "foodLicenseNo": "JY110000XXXXXXXX",
        "foodLicenseImg": "https://xxx.com/food.jpg",
        "legalPersonName": "李四",
        "legalPersonIdCard": "110101199001011234",
        "legalPersonIdCardImg": "https://xxx.com/idcard.jpg",
        "bankName": "中国工商银行",
        "bankAccount": "6222021234567890123",
        "alipayAccount": "13800138000@alipay.com",
        "status": 0,
        "statusDesc": "待审核",
        "createTime": "2026-03-05T10:00:00",
        "auditTime": null,
        "rejectReason": null
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| businessLicenseNo | String | 营业执照号 |
| businessLicenseImg | String | 营业执照图片URL |
| foodLicenseNo | String | 食品经营许可证号 |
| foodLicenseImg | String | 食品经营许可证图片URL |
| legalPersonName | String | 法人姓名 |
| legalPersonIdCard | String | 法人身份证号 |
| legalPersonIdCardImg | String | 法人身份证图片URL |
| bankName | String | 开户银行 |
| bankAccount | String | 银行账号 |
| alipayAccount | String | 支付宝账号 |
| auditTime | LocalDateTime | 审核时间 |
| rejectReason | String | 驳回原因 |

---

### 8.3 审核通过

**接口地址**：`POST /admin/merchant/audit/approve/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| remark | String | 否 | 备注 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 8.4 审核拒绝

**接口地址**：`POST /admin/merchant/audit/reject/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reason | String | 是 | 拒绝原因 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 8.5 启用商户

**接口地址**：`POST /admin/merchant/enable/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 8.6 禁用/封禁商户

**接口地址**：`POST /admin/merchant/disable/{merchantId}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reason | String | 是 | 封禁原因 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

---

### 8.7 违规记录列表

**接口地址**：`GET /admin/merchant/violation/list`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantName | String | 否 | 商户名称（模糊搜索） |
| violationType | Integer | 否 | 违规类型：1-商品违规，2-订单违规，3-服务违规，4-其他 |
| violationLevel | Integer | 否 | 违规等级：1-轻微，2-一般，3-严重，4-极其严重 |
| status | Integer | 否 | 处理状态：0-待处理，1-已处理，2-已申诉 |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| pageNum | Integer | 否 | 当前页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "records": [
            {
                "id": 1,
                "merchantId": 1,
                "merchantName": "测试餐饮店",
                "violationType": 1,
                "violationTypeDesc": "商品违规",
                "violationLevel": 2,
                "violationLevelDesc": "一般",
                "description": "商品图片不符合规范",
                "handleType": 1,
                "handleTypeDesc": "警告",
                "penaltyAmount": 0,
                "remark": null,
                "orderId": null,
                "orderNo": null,
                "status": 0,
                "statusDesc": "待处理",
                "createTime": "2026-03-05T10:00:00"
            }
        ],
        "total": 10,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

---

### 8.8 创建违规记录

**接口地址**：`POST /admin/merchant/violation/create`

**请求头**：需要管理员Token

**请求体**：

```json
{
    "merchantId": 1,
    "violationType": 1,
    "violationLevel": 2,
    "description": "商品图片不符合规范",
    "handleType": 1,
    "penaltyAmount": 0,
    "remark": "首次违规，警告处理",
    "orderId": null,
    "orderNo": null
}
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| merchantId | Long | 是 | 商户ID |
| violationType | Integer | 是 | 违规类型：1-商品违规，2-订单违规，3-服务违规，4-其他 |
| violationLevel | Integer | 是 | 违规等级：1-轻微，2-一般，3-严重，4-极其严重 |
| description | String | 是 | 违规描述 |
| handleType | Integer | 否 | 处理方式：1-警告，2-罚款，3-限单，4-封禁 |
| penaltyAmount | Long | 否 | 处罚金额（分） |
| remark | String | 否 | 备注 |
| orderId | Long | 否 | 关联订单ID |
| orderNo | String | 否 | 关联订单号 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": 1
}
```

---

### 8.9 处理违规记录

**接口地址**：`POST /admin/merchant/violation/handle/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 违规记录ID |

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| handleType | Integer | 是 | 处理方式：1-警告，2-罚款，3-限单，4-封禁 |
| remark | String | 否 | 备注 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": null
}
```

**说明**：
- 处理方式是封禁时，会同时封禁对应商户

---

## 九、用户管理接口

### 9.1 用户列表

**接口地址**：`GET /admin/user/list`

**请求头**：需要管理员Token

**权限要求**：`user:list` 或 `*:*`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 状态筛选：0-禁用，1-正常 |
| keyword | String | 否 | 关键词搜索（昵称/手机号） |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |

**请求示例**：

```
GET /admin/user/list
GET /admin/user/list?status=1
GET /admin/user/list?keyword=138
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "records": [
            {
                "id": 1,
                "openid": "oXXXX-xxxxxxxxxxxxxxxx",
                "phone": "13800138000",
                "nickname": "测试用户",
                "avatar": "https://example.com/avatar.jpg",
                "balance": 10000.00,
                "savedFoodWeight": 5.00,
                "carbonReduction": 2.00,
                "status": 1,
                "statusDesc": "正常",
                "createTime": "2026-03-01T10:00:00"
            }
        ],
        "total": 100,
        "size": 10,
        "current": 1,
        "pages": 10
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |
| openid | String | 微信openid |
| phone | String | 手机号 |
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| balance | BigDecimal | 账户余额（分） |
| savedFoodWeight | BigDecimal | 拯救食物重量（kg） |
| carbonReduction | BigDecimal | 减碳量（kg CO₂） |
| status | Integer | 状态：0-禁用，1-正常 |
| statusDesc | String | 状态描述 |

---

### 9.2 禁用用户

**接口地址**：`POST /admin/user/disable/{id}`

**请求头**：需要管理员Token

**权限要求**：`user:list:disable` 或 `*:*`

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求示例**：

```
POST /admin/user/disable/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

**说明**：
- 禁用后用户无法登录和下单

---

### 9.3 启用用户

**接口地址**：`POST /admin/user/enable/{id}`

**请求头**：需要管理员Token

**权限要求**：`user:list:enable` 或 `*:*`

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求示例**：

```
POST /admin/user/enable/1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

## 十、即时通讯接口

### 10.1 获取WebSocket连接信息

**接口地址**：`GET /im/ws/info`

**请求头**：需要Token（用户/商户/平台）

**说明**：获取WebSocket连接地址和专用Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "wsUrl": "ws://localhost:8888/ws",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "userId": 1,
        "userType": 1
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| wsUrl | String | WebSocket连接地址 |
| token | String | WebSocket专用Token |
| userId | Long | 用户ID |
| userType | Integer | 用户类型：1-用户，2-商户，3-平台 |

---

### 10.2 创建与商户的会话（用户端）

**接口地址**：`POST /im/session/merchant`

**请求头**：需要用户Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |
| storeId | Long | 否 | 门店ID |
| storeName | String | 否 | 门店名称 |
| orderId | Long | 否 | 订单ID |
| orderNo | String | 否 | 订单编号 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "sessionNo": "IM202603060001",
        "sessionType": 1,
        "userId": 100,
        "merchantId": 10,
        "storeName": "测试门店",
        "lastMessage": null,
        "unreadUserCount": 0,
        "unreadMerchantCount": 0,
        "status": 1,
        "createTime": "2026-03-06T10:00:00"
    }
}
```

---

### 10.3 创建与平台的会话（用户端）

**接口地址**：`POST /im/session/platform`

**请求头**：需要用户Token

**说明**：用户联系平台客服

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 2,
        "sessionNo": "IM202603060002",
        "sessionType": 2,
        "userId": 100,
        "lastMessage": null,
        "unreadUserCount": 0,
        "unreadPlatformCount": 0,
        "status": 1,
        "createTime": "2026-03-06T10:00:00"
    }
}
```

---

### 10.4 平台创建与用户的会话

**接口地址**：`POST /im/session/platform/user`

**请求头**：需要平台管理员Token

**权限要求**：`im:session` 或 `*:*`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 2,
        "sessionNo": "IM202603060002",
        "sessionType": 2,
        "userId": 100,
        "status": 1
    }
}
```

---

### 10.5 平台创建与商户的会话

**接口地址**：`POST /im/session/platform/merchant`

**请求头**：需要平台管理员Token

**权限要求**：`im:session` 或 `*:*`

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merchantId | Long | 是 | 商户ID |
| merchantName | String | 否 | 商户名称 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 3,
        "sessionNo": "IM202603060003",
        "sessionType": 1,
        "userId": 0,
        "merchantId": 10,
        "storeName": "测试商户",
        "status": 1
    }
}
```

---

### 10.6 获取会话列表

**接口地址**：`GET /im/session/list`

**请求头**：需要Token（用户/商户/平台）

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认20） |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "records": [
            {
                "id": 1,
                "sessionNo": "IM202603060001",
                "sessionType": 1,
                "userId": 100,
                "merchantId": 10,
                "storeName": "测试门店",
                "lastMessage": "您好，请问有什么可以帮助您？",
                "lastMessageTime": "2026-03-06T10:30:00",
                "unreadUserCount": 0,
                "unreadMerchantCount": 1,
                "status": 1
            }
        ],
        "total": 5,
        "size": 20,
        "current": 1
    }
}
```

---

### 10.7 获取会话详情

**接口地址**：`GET /im/session/{sessionId}`

**请求头**：需要Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | Long | 是 | 会话ID |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "sessionNo": "IM202603060001",
        "sessionType": 1,
        "userId": 100,
        "merchantId": 10,
        "storeName": "测试门店",
        "orderId": 1001,
        "orderNo": "ORD202603060001",
        "lastMessage": "好的，感谢您的支持",
        "lastMessageTime": "2026-03-06T10:35:00",
        "unreadUserCount": 0,
        "unreadMerchantCount": 0,
        "status": 1,
        "createTime": "2026-03-06T10:00:00"
    }
}
```

---

### 10.8 获取消息列表

**接口地址**：`GET /im/message/list`

**请求头**：需要Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | Long | 是 | 会话ID |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认50） |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "records": [
            {
                "id": 1,
                "messageNo": "MSG202603060001",
                "sessionId": 1,
                "messageType": 1,
                "senderId": 100,
                "senderType": 1,
                "receiverId": 10,
                "receiverType": 2,
                "content": "您好，请问这个盲盒还有吗？",
                "isRead": 1,
                "createTime": "2026-03-06T10:05:00"
            },
            {
                "id": 2,
                "messageNo": "MSG202603060002",
                "sessionId": 1,
                "messageType": 1,
                "senderId": 10,
                "senderType": 2,
                "receiverId": 100,
                "receiverType": 1,
                "content": "您好，还有的，欢迎下单！",
                "isRead": 1,
                "createTime": "2026-03-06T10:06:00"
            }
        ],
        "total": 2,
        "size": 50,
        "current": 1
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| messageType | Integer | 消息类型：1-文本，2-图片，3-文件 |
| senderType | Integer | 发送者类型：1-用户，2-商户，3-平台 |
| isRead | Integer | 是否已读：0-未读，1-已读 |

---

### 10.9 发送消息

**接口地址**：`POST /im/message/send`

**请求头**：需要Token

**请求参数**：

```json
{
    "sessionId": 1,
    "content": "好的，感谢您的支持！",
    "messageType": 1,
    "attachmentUrl": null,
    "attachmentName": null
}
```

**参数说明**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | Long | 是 | 会话ID |
| content | String | 是 | 消息内容 |
| messageType | Integer | 否 | 消息类型（默认1-文本） |
| attachmentUrl | String | 否 | 附件URL |
| attachmentName | String | 否 | 附件名称 |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 3,
        "messageNo": "MSG202603060003",
        "sessionId": 1,
        "messageType": 1,
        "senderId": 100,
        "senderType": 1,
        "content": "好的，感谢您的支持！",
        "status": 1,
        "isRead": 0,
        "createTime": "2026-03-06T10:40:00"
    }
}
```

---

### 10.10 撤回消息

**接口地址**：`POST /im/message/recall`

**请求头**：需要Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| messageNo | String | 是 | 消息编号 |

**说明**：只能撤回自己发送的消息

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

### 10.11 结束会话

**接口地址**：`POST /im/session/end/{sessionId}`

**请求头**：需要Token

**权限要求**：会话参与者或平台管理员（`im:session:end` 或 `*:*`）

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sessionId | Long | 是 | 会话ID |

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

## 十一、平台管理权限接口

### 11.1 角色管理

#### 11.1.1 角色列表

**接口地址**：`GET /admin/role/list`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 否 | 关键词搜索（角色名称/编码） |
| status | Integer | 否 | 状态筛选：0-禁用，1-启用 |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |

**请求示例**：

```
GET /admin/role/list
GET /admin/role/list?status=1
GET /admin/role/list?keyword=管理员
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "roleCode": "SUPER_ADMIN",
                "roleName": "超级管理员",
                "roleDesc": "拥有所有权限",
                "isPreset": 1,
                "status": 1,
                "createTime": "2026-03-01T10:00:00"
            },
            {
                "id": 2,
                "roleCode": "AUDITOR",
                "roleName": "审核员",
                "roleDesc": "负责商户入驻审核",
                "isPreset": 1,
                "status": 1,
                "createTime": "2026-03-01T10:00:00"
            }
        ],
        "total": 5,
        "page": 1,
        "size": 10
    }
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| isPreset | Integer | 是否预设角色：1-是，0-否 |

#### 9.1.2 创建角色

**接口地址**：`POST /admin/role/create`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleName | String | 是 | 角色名称 |
| roleCode | String | 是 | 角色编码 |
| roleDesc | String | 否 | 角色描述 |
| status | Integer | 否 | 状态：0-禁用，1-启用（默认1） |

**请求示例**：

```json
{
    "roleName": "运营经理",
    "roleCode": "OPERATOR_MANAGER",
    "roleDesc": "负责运营管理工作",
    "status": 1
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 6,
        "roleCode": "OPERATOR_MANAGER",
        "roleName": "运营经理",
        "roleDesc": "负责运营管理工作",
        "status": 1
    }
}
```

#### 11.1.3 更新角色

**接口地址**：`PUT /admin/role/update`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 角色ID |
| roleName | String | 否 | 角色名称 |
| roleDesc | String | 否 | 角色描述 |
| status | Integer | 否 | 状态 |

**请求示例**：

```json
{
    "id": 6,
    "roleName": "运营总监",
    "roleDesc": "负责整体运营管理工作"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

#### 9.1.4 删除角色

**接口地址**：`DELETE /admin/role/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 角色ID |

**请求示例**：

```
DELETE /admin/role/6
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

**说明**：预设角色不允许删除

#### 11.1.5 分配权限

**接口地址**：`POST /admin/role/assign`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleId | Long | 是 | 角色ID |
| permissionIds | List | 是 | 权限ID列表 |

**请求示例**：

```json
{
    "roleId": 2,
    "permissionIds": [15, 16, 17, 18, 19, 20, 21, 22]
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

---

### 11.2 权限管理

#### 11.2.1 获取权限树

**接口地址**：`GET /admin/permission/tree`

**请求头**：需要管理员Token

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": [
        {
            "id": 1,
            "permissionCode": "system",
            "permissionName": "系统管理",
            "permissionType": 1,
            "parentId": null,
            "parentCode": "",
            "menuPath": "/system",
            "menuIcon": "Setting",
            "sortOrder": 1,
            "children": [
                {
                    "id": 2,
                    "permissionCode": "system:role",
                    "permissionName": "角色管理",
                    "permissionType": 1,
                    "parentId": 1,
                    "parentCode": "system",
                    "menuPath": "/system/role",
                    "children": [
                        {
                            "id": 3,
                            "permissionCode": "system:role:view",
                            "permissionName": "查看角色",
                            "permissionType": 2,
                            "parentId": 2,
                            "parentCode": "system:role"
                        }
                    ]
                }
            ]
        }
    ]
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| permissionType | Integer | 权限类型：1-菜单，2-按钮 |
| parentCode | String | 父权限编码（用于构建树形结构） |

---

### 9.3 账号管理

#### 9.3.1 账号列表

**接口地址**：`GET /admin/account/list`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 否 | 关键词搜索（用户名/姓名/手机号） |
| status | Integer | 否 | 状态筛选：0-禁用，1-启用 |
| roleId | Long | 否 | 角色ID筛选 |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |

**请求示例**：

```
GET /admin/account/list
GET /admin/account/list?roleId=1
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "username": "admin",
                "realName": "系统管理员",
                "phone": "13800138000",
                "email": "admin@example.com",
                "roleId": 1,
                "roleName": "超级管理员",
                "status": 1,
                "lastLoginTime": "2026-03-05T14:00:00",
                "createTime": "2026-03-01T10:00:00"
            }
        ],
        "total": 10,
        "page": 1,
        "size": 10
    }
}
```

#### 9.3.2 创建账号

**接口地址**：`POST /admin/account/create`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| realName | String | 否 | 真实姓名 |
| phone | String | 否 | 手机号 |
| email | String | 否 | 邮箱 |
| roleIds | String | 否 | 角色ID列表（逗号分隔） |
| dataScope | Integer | 否 | 数据权限：1-全部，2-本部门，3-仅本人（默认1） |
| status | Integer | 否 | 状态：0-禁用，1-启用（默认1） |

**请求示例**：

```json
{
    "username": "operator1",
    "password": "123456",
    "realName": "运营人员",
    "phone": "13900139001",
    "email": "operator@example.com",
    "roleIds": "5",
    "dataScope": 1,
    "status": 1
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 10,
        "username": "operator1",
        "realName": "运营人员"
    }
}
```

#### 9.3.3 更新账号

**接口地址**：`PUT /admin/account/update`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 账号ID |
| realName | String | 否 | 真实姓名 |
| phone | String | 否 | 手机号 |
| email | String | 否 | 邮箱 |
| roleIds | String | 否 | 角色ID列表 |
| dataScope | Integer | 否 | 数据权限 |
| status | Integer | 否 | 状态 |

**请求示例**：

```json
{
    "id": 10,
    "realName": "运营主管",
    "roleIds": "5,6"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

#### 9.3.4 删除账号

**接口地址**：`DELETE /admin/account/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 账号ID |

**请求示例**：

```
DELETE /admin/account/10
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

#### 9.3.5 重置密码

**接口地址**：`POST /admin/account/resetPwd/{id}`

**请求头**：需要管理员Token

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 账号ID |

**请求示例**：

```
POST /admin/account/resetPwd/10
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功"
}
```

**说明**：重置后密码为 "123456"

#### 9.3.6 修改密码

**接口地址**：`PUT /admin/account/password`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | String | 是 | 原密码 |
| newPassword | String | 是 | 新密码 |

**请求示例**：

```json
{
    "oldPassword": "123456",
    "newPassword": "abcdef"
}
```

**响应示例**：

```json
{
    "code": 200,
    "message": "密码修改成功，请重新登录"
}
```

---

### 9.4 操作日志

#### 9.4.1 日志列表

**接口地址**：`GET /admin/log/list`

**请求头**：需要管理员Token

**请求参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| module | String | 否 | 操作模块 |
| operationType | String | 否 | 操作类型 |
| adminId | Long | 否 | 操作人ID |
| startTime | String | 否 | 开始时间（YYYY-MM-DD） |
| endTime | String | 否 | 结束时间（YYYY-MM-DD） |
| page | Integer | 否 | 页码（默认1） |
| size | Integer | 否 | 每页大小（默认20） |

**操作类型说明**：

| 类型值 | 说明 |
|--------|------|
| LOGIN | 登录 |
| LOGOUT | 登出 |
| ADD | 新增 |
| EDIT | 编辑 |
| DELETE | 删除 |
| AUDIT | 审核 |
| EXPORT | 导出 |
| RESET_PWD | 重置密码 |

**请求示例**：

```
GET /admin/log/list?module=系统管理
GET /admin/log/list?operationType=ADD&startTime=2026-03-01&endTime=2026-03-05
```

**响应示例**：

```json
{
    "code": 200,
    "message": "成功",
    "data": {
        "list": [
            {
                "id": 1,
                "adminId": 1,
                "adminName": "admin",
                "operationType": "ADD",
                "module": "账号管理",
                "content": "{\"username\": \"testuser\"}",
                "ip": "127.0.0.1",
                "result": 1,
                "createTime": "2026-03-05T10:30:00"
            }
        ],
        "total": 100,
        "page": 1,
        "size": 20
    }
}
```

---

## 十、订单状态流转

### 10.1 状态说明

| 状态值 | 状态名 | 说明 |
|--------|--------|------|
| 0 | 待支付 | 订单创建后的初始状态 |
| 1 | 已支付 | 用户完成支付 |
| 2 | 待取餐 | 商户已备餐，等待用户取货 |
| 3 | 已完成 | 商户核销成功 |
| 4 | 已取消 | 用户取消或超时取消 |
| 5 | 已退款 | 商户同意退款，退款成功 |
| 6 | 退款中 | 用户申请退款，等待商户审核（超时24小时自动退款）|

### 10.2 状态流转图

```
                                    ┌──────────────────────────────────────┐
                                    │                                      ▼
创建订单 ──────▶ 待支付(0) ──────▶ 已支付(1) ──────▶ 待取餐(2) ──────▶ 已完成(3)
                    │                   │                   │
                    │                   │                   │
                    ▼                   ▼                   ▼
              已取消(4)            退款中(6)           退款中(6)
                                       │                   │
                                       │                   │
                              ┌────────┴────────┬──────────┴────────┐
                              │                 │                    │
                              ▼                 ▼                    ▼
                          已退款(5)         已支付(1)            已退款(5)
                         (商户同意)        (商户拒绝)        (24小时超时自动退款)
```

### 10.3 状态流转接口

| 操作 | 状态变化 | 接口 | 权限 |
|------|---------|------|------|
| 创建订单 | 无 → 0 | POST /order/create | 用户 |
| 支付订单 | 0 → 1 | POST /payment/balance/{orderNo} | 用户 |
| 取消订单 | 0 → 4 | POST /order/cancel/{orderNo} | 用户 |
| 备餐完成 | 1 → 2 | POST /order/ready/{orderNo} | 商户 |
| 核销订单 | 1或2 → 3 | POST /order/verify/{pickupCode} | 商户 |
| 申请退款 | 1或2 → 6 | POST /order/refund/{orderNo} | 用户 |
| 同意退款 | 6 → 5 | POST /order/refund/approve/{orderNo} | 商户 |
| 拒绝退款 | 6 → 1 | POST /order/refund/reject/{orderNo} | 商户 |

---

## 十一、业务流程

### 11.1 完整购物流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              用户端流程                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 浏览商品                                                                │
│     GET /product/list                                                      │
│           ↓                                                                 │
│  2. 创建订单                                                                │
│     POST /order/create                                                     │
│           ↓                                                                 │
│  3. 选择支付方式                                                            │
│     ├─ 微信支付: POST /wechat/pay/create                                  │
│     └─ 余额支付: POST /payment/balance/{orderNo}                         │
│           ↓                                                                 │
│  4. 支付成功 → 订单状态变为"已支付"                                        │
│                                                                             │
│  5. 到店取货                                                                │
│     ├─ 用户展示提货码                                                       │
│     └─ 商户核销: POST /order/verify/{pickupCode}                          │
│           ↓                                                                 │
│  6. 核销成功 → 订单状态变为"已完成" + 分账给商户                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 11.2 商户入驻流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              商户入驻流程                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 提交入驻申请                                                            │
│     POST /merchant/register                                                │
│           ↓                                                                 │
│  2. 平台审核                                                                │
│     POST /admin/audit/approve/{merchantId}                                │
│           ↓                                                                 │
│  3. 商户完善信息                                                            │
│     POST /merchant/complete                                                │
│           ↓                                                                 │
│  4. 商户添加门店                                                            │
│     POST /store/create                                                     │
│           ↓                                                                 │
│  5. 商户添加商品                                                            │
│     POST /product/create                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 11.3 退款流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              退款流程                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 用户申请退款                                                            │
│     POST /order/refund/{orderNo}                                          │
│     订单状态: 已支付(1) 或 待取餐(2) → 退款中(6)                          │
│           ↓                                                                 │
│  2. 商户处理                                                                │
│     ├─ 同意退款: POST /order/refund/approve/{orderNo}                    │
│     │           订单状态: 退款中(6) → 已退款(5)                            │
│     │           退款到用户余额                                              │
│     │                                                                     │
│     └─ 拒绝退款: POST /order/refund/reject/{orderNo}                      │
│                   订单状态: 退款中(6) → 已支付(1)                         │
│                                                                             │
│  3. 超时自动退款                                                            │
│     退款申请后24小时未处理 → 自动退款                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 十二、并发安全机制

### 12.1 库存超卖防护

**问题场景**：多个用户同时抢购同一商品，可能导致超卖

**解决方案**：使用原子扣减SQL + 数据库行锁

```sql
UPDATE product_info 
SET remaining_stock = remaining_stock - #{quantity}
WHERE id = #{productId}
  AND remaining_stock >= #{quantity}
  AND status = 1
  AND deleted = 0
```

### 12.2 余额并发扣减

**问题场景**：用户余额并发扣减（如支付、退款）

**解决方案**：乐观锁（version字段）

```sql
UPDATE user_info SET balance = balance - #{amount},
       version = version + 1
WHERE id = #{userId} AND version = #{expectedVersion}
  AND balance >= #{amount}
```

### 12.3 定时任务分布式锁

**问题场景**：多实例部署，定时任务重复执行

**解决方案**：Redis SETNX 分布式锁

```java
stringRedisTemplate.opsForValue()
    .setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
```

### 12.4 异步分账

**问题场景**：核销后分账逻辑阻塞主流程

**解决方案**：RabbitMQ 异步处理

```
核销订单 → 发送MQ消息 → 消费者异步处理分账
```

### 12.5 缓存雪崩防护

**问题场景**：大量缓存同时过期

**解决方案**：TTL随机化

```java
private long getRandomTTL(long baseTTL) {
    long variance = (long) (baseTTL * 0.1);
    return baseTTL + (long) (Math.random() * variance * 2 - variance);
}
```

---

## 十三、附录

### 13.1 错误码汇总

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/Token无效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 13.2 业务错误码

| 错误码 | 说明 |
|--------|------|
| 1001 | 商品不存在 |
| 1002 | 商品已下架 |
| 1003 | 库存不足 |
| 2001 | 订单不存在 |
| 2002 | 订单状态异常 |
| 2003 | 提货码无效 |
| 3001 | 余额不足 |
| 3002 | 退款失败 |
| 4001 | 商户不存在 |
| 4002 | 商户未通过审核 |

### 13.3 版本信息

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2026-03-01 | 初始版本 |

---

*文档最后更新时间：2026-03-09*

### 7.2 状态流转图

```
                                    ┌──────────────────────────────────────┐
                                    │                                      ▼
创建订单 ──────▶ 待支付(0) ──────▶ 已支付(1) ──────▶ 待取餐(2) ──────▶ 已完成(3)
                    │                   │                   │
                    │                   │                   │
                    ▼                   ▼                   ▼
              已取消(4)            退款中(6)           退款中(6)
                                       │                   │
                                       │                   │
                              ┌────────┴────────┬──────────┴────────┐
                              │                 │                    │
                              ▼                 ▼                    ▼
                          已退款(5)         已支付(1)            已退款(5)
                         (商户同意)        (商户拒绝)        (24小时超时自动退款)
```

### 7.3 状态流转接口

| 操作 | 状态变化 | 接口 | 权限 |
|------|---------|------|------|
| 创建订单 | 无 → 0 | POST /order/create | 用户 |
| 支付订单 | 0 → 1 | POST /order/pay/{orderNo} | 用户 |
| 取消订单 | 0 → 4 | POST /order/cancel/{orderNo} | 用户 |
| 备餐完成 | 1 → 2 | POST /order/ready/{orderNo} | 商户 |
| 核销订单 | 1或2 → 3 | POST /order/verify/{pickupCode} | 商户 |
| 申请退款 | 1或2 → 6 | POST /order/refund/{orderNo} | 用户 |
| 同意退款 | 6 → 5 | POST /order/refund/approve/{orderNo} | 商户 |
| 拒绝退款 | 6 → 1 | POST /order/refund/reject/{orderNo} | 商户 |
| 超时自动退款 | 6 → 5 | 系统定时任务（每小时执行） | 系统 |

### 7.4 并发安全说明

所有状态变更接口均使用数据库原子更新，保证并发安全：

- **备餐完成 + 申请退款**：只有一个操作会成功，另一个返回"订单状态已变更"
- **核销订单 + 申请退款**：只有一个操作会成功，另一个返回"订单状态已变更"

### 7.5 库存并发控制

系统采用**库存预占机制**保证高并发场景下的库存安全：

#### 库存操作流程

```
创建订单 ──▶ lockStock（锁定库存）
    │
    ├── 支付成功 ──▶ confirmDeduct（确认扣减）
    │
    └── 取消/超时 ──▶ releaseStock（释放库存）

退款成功 ──▶ increaseStock（恢复库存）
```

#### 库存表设计

| 字段 | 说明 |
|------|------|
| total_stock | 总库存 |
| available_stock | 可用库存（可被锁定） |
| locked_stock | 锁定库存（待支付订单占用） |

#### 并发安全保证

- **数据库行锁**：所有库存操作使用原子 SQL，利用数据库行锁保证并发安全
- **事务保证**：UPDATE 和 SELECT 在同一 `@Transactional` 事务中，行锁持续到事务提交
- **库存变更日志**：所有库存变更记录到 `inventory_log` 表，支持审计追溯
- **超时自动释放**：未支付订单超时（15分钟）自动取消，释放锁定库存
- **日志准确性保证**：
  - 整个操作在同一事务中执行，行锁持续到事务提交
  - UPDATE 成功后立即 SELECT 获取 after 值（事务内查询，数据一致）
  - before 值通过 after + changeAmount 反推（数学上正确）
  - 高并发下日志记录依然准确

#### 定时任务

| 任务 | 执行频率 | 功能 |
|------|---------|------|
| 超时订单取消 | 每分钟 | 取消超时未支付订单，释放库存 |
| 超时退款批准 | 每5分钟 | 24小时未处理退款申请自动批准 |

### 7.6 超时自动退款机制

系统通过定时任务自动处理超时未审核的退款申请：

- **执行频率**：每小时整点执行一次
- **超时时间**：退款申请提交后24小时
- **处理逻辑**：
  1. 查询所有"退款中(6)"状态且`refundApplyTime`超过24小时的订单
  2. 自动将订单状态更新为"已退款(5)"
  3. 自动恢复商品库存
  4. 在退款原因中追加"（系统自动退款：商户超时未处理）"标识

---



