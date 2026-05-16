package com.example.bank.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class OrderRequest {

    @NotBlank(message = "成員編號不能為空")
    private String memberId;


    private List<OrderDto> Items;
}
