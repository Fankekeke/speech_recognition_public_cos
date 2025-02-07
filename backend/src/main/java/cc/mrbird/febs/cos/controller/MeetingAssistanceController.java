package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.MeetingAssistance;
import cc.mrbird.febs.cos.service.IMeetingAssistanceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/meeting-assistance")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeetingAssistanceController {

    private final IMeetingAssistanceService meetingAssistanceService;

    /**
     * 分页获取要点提炼信息
     *
     * @param page              分页对象
     * @param meetingAssistance 要点提炼信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<MeetingAssistance> page, MeetingAssistance meetingAssistance) {
        return R.ok();
    }

    /**
     * 查询要点提炼信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(meetingAssistanceService.getById(id));
    }

    /**
     * 查询要点提炼信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(meetingAssistanceService.list());
    }

    /**
     * 新增要点提炼信息
     *
     * @param meetingAssistance 要点提炼信息
     * @return 结果
     */
    @PostMapping
    public R save(MeetingAssistance meetingAssistance) {
        return R.ok(meetingAssistanceService.save(meetingAssistance));
    }

    /**
     * 修改要点提炼信息
     *
     * @param meetingAssistance 要点提炼信息
     * @return 结果
     */
    @PutMapping
    public R edit(MeetingAssistance meetingAssistance) {
        return R.ok(meetingAssistanceService.updateById(meetingAssistance));
    }

    /**
     * 删除要点提炼信息
     *
     * @param ids ids
     * @return 要点提炼信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(meetingAssistanceService.removeByIds(ids));
    }
}
