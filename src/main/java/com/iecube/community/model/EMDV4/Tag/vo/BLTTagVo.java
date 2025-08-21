package com.iecube.community.model.EMDV4.Tag.vo;

import com.iecube.community.model.EMDV4.Tag.entity.BLTTag;
import lombok.Data;

@Data
public class BLTTagVo extends BLTTag {
   private long bookId;
   private long targetId;
   private String targetName;
}
