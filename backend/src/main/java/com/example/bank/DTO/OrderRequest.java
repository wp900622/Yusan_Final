package com.example.bank.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class OrderRequest {

    // memberId 不再由前端傳入，後端從 JWT 解析登入者
    private List<OrderDto> Items;
}
