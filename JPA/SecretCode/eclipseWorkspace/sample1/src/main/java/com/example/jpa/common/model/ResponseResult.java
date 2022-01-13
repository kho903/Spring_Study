package com.example.jpa.common.model;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.model.ResponseMessage;
import org.springframework.http.ResponseEntity;

public class ResponseResult {


    public static ResponseEntity<?> fail(String message) {
        return ResponseEntity.badRequest().body(ResponseMessage.fail(message));
    }

    public static ResponseEntity<?> success() {
        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    public static ResponseEntity<?> result(ServiceResult result) {
        if (result.isFail()) {
            return fail(result.getMessage());
        }
        return success();
    }
}
