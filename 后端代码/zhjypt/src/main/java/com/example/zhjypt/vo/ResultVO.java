package com.example.zhjypt.vo;

/**
 * 统一响应结果封装类
 *
 * 所有 Controller 接口均返回此对象，前端通过 flag 判断业务是否成功。
 *
 * @param <T> 返回数据的泛型类型
 */
public class ResultVO<T> {

    /** 本次业务操作的响应消息（成功/失败原因） */
    private String reason;

    /** 本次业务操作的响应状态：true 成功，false 失败 */
    private boolean flag;

    /** 返回的业务数据 */
    private T result;

    /** 分页查询时的总记录数 */
    private long total;

    // ==================== 静态工厂方法 ====================

    /**
     * 成功响应（含数据 + 分页总数）
     *
     * @param reason 成功消息
     * @param result 返回数据
     * @param total  总记录数
     */
    public static <T> ResultVO<T> success(String reason, T result, long total) {
        ResultVO<T> model = new ResultVO<>();
        model.setFlag(true);
        model.setReason(reason);
        model.setResult(result);
        model.setTotal(total);
        return model;
    }

    /**
     * 成功响应（含数据，无分页）
     *
     * @param reason 成功消息
     * @param result 返回数据
     */
    public static <T> ResultVO<T> success(String reason, T result) {
        ResultVO<T> model = new ResultVO<>();
        model.setFlag(true);
        model.setReason(reason);
        model.setResult(result);
        return model;
    }

    /**
     * 成功响应（无数据，仅消息）
     *
     * @param reason 成功消息
     */
    public static ResultVO success(String reason) {
        ResultVO model = new ResultVO<>();
        model.setFlag(true);
        model.setReason(reason);
        return model;
    }

    /**
     * 失败响应
     *
     * @param reason 失败原因
     */
    public static ResultVO fail(String reason) {
        ResultVO model = new ResultVO<>();
        model.setFlag(false);
        model.setReason(reason);
        return model;
    }

    // ==================== Getter / Setter ====================

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isFlag() { return flag; }
    public void setFlag(boolean flag) { this.flag = flag; }

    public T getResult() { return result; }
    public void setResult(T result) { this.result = result; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    @Override
    public String toString() {
        return "ResultVO{reason='" + reason + "', flag=" + flag + ", result=" + result + '}';
    }
}
