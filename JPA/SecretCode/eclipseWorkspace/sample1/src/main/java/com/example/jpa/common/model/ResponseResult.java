package com.example.jpa.common.model;

import com.example.jpa.board.entity.BoardBadReport;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.model.ResponseMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseResult {


    public static ResponseEntity<?> fail(String message) {
        return fail(message, null);
    }

    public static ResponseEntity<?> fail(String message, Object data) {
        return ResponseEntity.badRequest().body(ResponseMessage.fail(message, data));
    }

    public static ResponseEntity<?> success() {
        return success(null);
    }

    public static ResponseEntity<?> success(Object data) {
        return ResponseEntity.ok().body(ResponseMessage.success(data));
    }

    public static ResponseEntity<?> result(ServiceResult result) {
        if (result.isFail()) {
            return fail(result.getMessage());
        }
        return success();
    }
}
