# 🚀 Merchant Rest API

## EmvMpmQr 생성

> [BcMpm가맹점의 Emv규격 MpmQr 생성]

### 📋 개요

| 항목         | 내용                             |
|:-----------|:-------------------------------|
| **Method** | `POST`                         |
| **URL**    | `/qrpay/api/v1/merchant/mpmqr` |
| **Auth**   | `Required (Bearer Token)`      |

---

### 📥 1. Request (요청)

#### 1-1. Headers

- `Authorization: Bearer (token)`
- `Content-Type: application/json`

#### 1-2. Request Body Parameters

| 필드명           | 타입     | 필수 여부 | 설명                                                                  |
|:--------------|:-------|:-----:|:--------------------------------------------------------------------|
| `pim`         | String |  필수   | MpmQr PIM. 아래 값이 유효하다. <br/> - `STATIC`: 고정형 <br/> - `DYNAMIC`: 변동형 |
| `amount`      | Long   |  선택   | MPMQR의 가격. `DYNAMIC` 일때 필수 값.                                       |
| `installment` | Long   |  선택   | MPMQR의 할부개월수. `DYNAMIC` 일때 필수 값.                                    |

**Request Body Example**

```json 
{
  "pim": "STATIC"
}
```

```json 
{
  "pim": "DYNAMIC",
  "amount": 50000,
  "installment": 00
}
```

### 📥 2. Response (응답)

#### 2-1. Response Body Parameters

| 필드명        | 타입                              | 필수 여부 | 설명                                 |
|:-----------|:--------------------------------|:-----:|:-----------------------------------|
| `code`     | String                          |  필수   | 응답코드.  성공 `MP0000` 이외는 실패.         |
| `message`  | String                          |  필수   | 응답메시지                              |
| `memberId` | String                          |  선택   | 인증 memberId                        |
| `loginId`  | String                          |  선택   | 인증 loginId                         |
| `data`     | [MpmQrInfoResDto](#mpm-qr-info) |  선택   | 응답데이터. `MpmQrInfoResDto` Object참조. |

**MpmQrInfoResDto Object**
<div id="mpm-qr-info"></div>

| 필드명             | 타입     | 필수 여부 | 설명                                       |
|:----------------|:-------|:-----:|:-----------------------------------------|
| `pim`           | String |  필수   | 응답 mpmqr의 pim. <br/> `STATIC`, `DYNAMIC` |
| `amount`        | String |  선택   | mpmqr의 가격. `DYNAMIC` 일때 필수.              |
| `installment`   | String |  선택   | mpmqr의 할부개월수 `DYNAMIC` 일때 필수.            |
| `qrBase64Image` | String |  필수   | mpmqr `png` base64 image.                |
| `merchantName`  | String |  필수   | mpmqr의 가맹점 이름                            |

### 📥 3. Error
