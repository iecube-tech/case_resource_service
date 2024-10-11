package com.iecube.community.model.pst_article_compose.controller;

import com.iecube.community.basecontroller.pst_article_compose.PSTArticleComposeBaseController;
import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import com.iecube.community.model.pst_article_compose.qo.ComposeQo;
import com.iecube.community.model.pst_article_compose.service.PSTArticleComposeService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/s/article/compose")
public class PSTArticleComposeController extends PSTArticleComposeBaseController {
    @Autowired
    private PSTArticleComposeService pstArticleComposeService;

    @GetMapping("/{articleId}/{index}")
    public JsonResult<PSTArticleCompose> pstArticleComposeDataGet(@PathVariable Integer articleId, @PathVariable Integer index){
        PSTArticleCompose pstArticleCompose = pstArticleComposeService.getCompose(articleId, index);
        return new JsonResult<>(OK, pstArticleCompose);
    }

    @PostMapping("/update/{composeId}")
    public JsonResult<PSTArticleCompose> pstArticleComposeValUpdate(@PathVariable Integer composeId, @RequestBody ComposeQo composeQo){
        pstArticleComposeService.composeUpdateVal(composeId, composeQo.getVal());
        PSTArticleCompose pstArticleCompose = pstArticleComposeService.composeUpdateResultStatus(composeId, composeQo.getResult());
        return new JsonResult<>(OK, pstArticleCompose);
    }

    @PostMapping("/upresult/{composeId}")
    public JsonResult<PSTArticleCompose> pstArticleComposeResultUpdate(@PathVariable Integer composeId, @RequestBody ComposeQo composeQo){
        PSTArticleCompose pstArticleCompose = pstArticleComposeService.composeUpdateResult(composeId, composeQo.getResult());
        return new JsonResult<>(OK, pstArticleCompose);
    }

    @GetMapping("/composes/{pstId}")
    public JsonResult<List> composeListByPstId(@PathVariable Integer pstId){
        List<PSTArticleCompose> composeList = pstArticleComposeService.composeListByPstId(pstId);
        return new JsonResult<>(OK, composeList);
    }
}
