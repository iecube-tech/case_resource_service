package com.iecube.community.model.elaborate_md.block.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md.block.entity.Block;
import com.iecube.community.model.elaborate_md.block.entity.BlockDetail;
import com.iecube.community.model.elaborate_md.block.qo.BlockQo;
import com.iecube.community.model.elaborate_md.block.service.BlockService;
import com.iecube.community.model.elaborate_md.block.vo.BlockVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elaborate/md/block")
public class BlockController extends BaseController {

    @Autowired
    private BlockService blockService;

    @PostMapping("/create")
    public JsonResult<BlockVo> createBlock(@RequestBody BlockQo blockQo){
        BlockVo res = blockService.createBlock(blockQo);
        return new JsonResult<>(OK, res);
    }

    @DeleteMapping("/del")
    public JsonResult<List<Block>> delBlock(@RequestBody Block block){
        blockService.delBlock(block);
        return new JsonResult<>(OK, blockService.getBlockListBySection(block.getParentId()));
    }

    @PostMapping("/sort")
    public JsonResult<List<Block>> batchUpSort(@RequestBody List<Block> blockList){
        blockService.batchUpBlockSort(blockList);
        return new JsonResult<>(OK, blockService.getBlockListBySection(blockList.get(0).getParentId()));
    }

    @GetMapping("/{sectionId}")
    public JsonResult<List<Block>> getBlockListBySectionId(@PathVariable long sectionId){
        return new JsonResult<>(OK, blockService.getBlockListBySection(sectionId));
    }

    @GetMapping("/vo/list")
    public JsonResult<List<BlockVo>> getBlockVoList(Long sectionId){
        List<BlockVo> res = blockService.getBlockVoListBySection(sectionId);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/detail")
    public JsonResult<BlockDetail> getBlockDetail(Long blockId){
        return new JsonResult<>(OK,blockService.getBlockDetailByBlock(blockId));
    }

    @PostMapping("/up/detail")
    public JsonResult<Void> upBlockDetail(@RequestBody BlockDetail blockDetail){
        blockService.upBlockDetail(blockDetail);
        return new JsonResult<>(OK);
    }


}
