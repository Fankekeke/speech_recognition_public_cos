package cc.mrbird.febs.cos.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FanK
 */
@Data
public class MindResult {

    private String id;

    private String topic;

    private List<MindResult> children = new ArrayList<>();

    public MindResult(String id, String topic) {
        this.id = id;
        this.topic = topic;
    }

    public MindResult() {}
}
