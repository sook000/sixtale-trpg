package org.infinity.sixtalebackend.global.common.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class DefaultResponse<T> {
    private int statusCode;
    private String responseMessage;
    private T data;

    //데이터가 없는경우 넘겨줄 response
    public DefaultResponse(final int statusCode,final String responseMessage) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.data = null;
    }

    public static<T> DefaultResponse<T> res(final int statusCode, final String responseMessage) {
        return res(statusCode, responseMessage, null);
    }

    //builder 사용
    public static<T> DefaultResponse<T> res(final int statusCode, final String responseMessage, final T t) {
        return DefaultResponse.<T>builder()
                .data(t)
                .statusCode(statusCode)
                .responseMessage(responseMessage)
                .build();
    }
}
