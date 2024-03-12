package com.iecube.community.model.iecube_device.mapper;

import com.iecube.community.CommunityApplication;
import com.iecube.community.model.iecube_device.entity.IecubeDevice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class IecubeDeviceMapperTests {
    @Autowired
    private IecubeDeviceMapper iecubeDeviceMapper;

    @Test
    public void all(){
        List<IecubeDevice> devices = iecubeDeviceMapper.all();
        System.out.println(devices);
    }
}
