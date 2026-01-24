# 1. Merchant Rest API

## 🚀 API Name: [여기에 API 이름 입력]

> [API에 대한 간략한 설명 한 줄]

### 📋 개요

| 항목         | 내용                                |
|:-----------|:----------------------------------|
| **Method** | `GET` / `POST` / `PUT` / `DELETE` |
| **URL**    | `/api/v1/resource-path`           |
| **Auth**   | `Required (Bearer Token)`         |

---

### 📥 Request (요청)

#### 1. Path Parameters / Query Parameters

| 필드명  | 타입   | 필수 여부 | 설명         |
|:-----|:-----|:-----:|:-----------|
| `id` | Long |  필수   | 리소스의 고유 ID |

#### 2. Request Body

```json
{
  "name": "string",
  "age": "integer"
}