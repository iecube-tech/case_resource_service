package com.iecube.community.model.markdown_compose.controller;

import com.iecube.community.basecontroller.markdown_compose.MarkdownComposeBaseController;
import com.iecube.community.model.markdown_compose.entity.MdArticleCompose;
import com.iecube.community.model.markdown_compose.service.MdArticleComposeService;
import com.iecube.community.model.markdown_compose.qo.ComposeQo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/t/article/compose")
public class MarkdownComposeController extends MarkdownComposeBaseController {

    @Autowired
    private MdArticleComposeService mdArticleComposeService;

    @GetMapping("/{mdArticleId}/{index}")
    public JsonResult<MdArticleCompose> markdownComposeDataGet(@PathVariable Integer mdArticleId, @PathVariable Integer index){
        MdArticleCompose mdArticleCompose = mdArticleComposeService.getCompose(mdArticleId, index);
        return new JsonResult<>(OK, mdArticleCompose);
    }

    @PostMapping("/upval/{composeId}")
    public JsonResult<MdArticleCompose> markdownComposeValUpdate(@PathVariable Integer composeId, @RequestBody ComposeQo composeQo){
        MdArticleCompose mdArticleCompose = mdArticleComposeService.mdComposerUpdateVal(composeId, composeQo.getVal());
        return new JsonResult<>(OK, mdArticleCompose);
    }

    @PostMapping("/upanswer/{composeId}")
    public JsonResult<MdArticleCompose> markdownComposeAnswerUpdate(@PathVariable Integer composeId, @RequestBody ComposeQo composeQo){
        MdArticleCompose mdArticleCompose = mdArticleComposeService.mdComposerUpdateAnswer(composeId, composeQo.getAnswer());
        return new JsonResult<>(OK,mdArticleCompose);
    }

    @PostMapping("/upscore/{composeId}")
    public JsonResult<MdArticleCompose> markdownComposeScoreUpdate(@PathVariable Integer composeId, @RequestBody ComposeQo composeQo){
        MdArticleCompose mdArticleCompose = mdArticleComposeService.mdComposerUpdateScore(composeId, composeQo.getScore());
        return new JsonResult<>(OK,mdArticleCompose);
    }
    @PostMapping("/upsubjective/{composeId}")
    public JsonResult<MdArticleCompose> markdownComposeSubjectiveUpdate(@PathVariable Integer composeId, @RequestBody ComposeQo composeQo){
        MdArticleCompose mdArticleCompose = mdArticleComposeService.mdComposerUpdateSubjective(composeId, composeQo.getSubjective());
        return new JsonResult<>(OK,mdArticleCompose);
    }
}
