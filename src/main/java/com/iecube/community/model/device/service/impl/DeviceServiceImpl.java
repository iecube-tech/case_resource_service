package com.iecube.community.model.device.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.device.dto.VncResponse;
import com.iecube.community.model.device.entity.*;
import com.iecube.community.model.device.mapper.DeviceMapper;
import com.iecube.community.model.device.qo.DeviceQo;
import com.iecube.community.model.device.qo.RemoteDeviceQo;
import com.iecube.community.model.device.service.DeviceService;
import com.iecube.community.model.device.service.ex.*;
import com.iecube.community.model.device.vo.DeviceVo;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.tcpClient.dto.DeviceDetail;
import com.iecube.community.model.tcpClient.dto.OnlineBoxResponse;
import com.iecube.community.model.tcpClient.dto.TcpMessageDto;
import com.iecube.community.model.tcpClient.ex.OnlineBoxResponseTimeoutException;
import com.iecube.community.model.tcpClient.ex.ReceivedMessageException;
import com.iecube.community.model.tcpClient.service.OnlineBoxTcpClient;
import com.iecube.community.model.teacher.entity.Teacher;
import com.iecube.community.model.teacher.mapper.TeacherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {
    /**
     * 新增设备
     * 新增控制器
     * 控制器添加设备
     * 编辑控制器
     * 编辑设备
     * 编辑控制器下设备
     *
     * 控制器下设备开启远程
     * 控制器下设备关闭远程
     *
     * 控制器下设备状态监测
     *
     */

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    public void addDevice(DeviceQo deviceQo, Integer user){
        //onlineBox
        Teacher teacher = teacherMapper.findById(user);
        if(deviceQo.getType().equals(0)){
            DeviceController oldDeviceController = deviceMapper.getDeviceControllerById(deviceQo.getId());
            if(oldDeviceController!=null){
                throw new OnlineBoxHasBeenUsedException("该远程设备已被使用，请检查设备ID");
            }
            DeviceController deviceController = new DeviceController();
            deviceController.setId(deviceQo.getId());
            deviceController.setSnId(deviceQo.getSnId());
            deviceController.setName(deviceQo.getName());
            deviceController.setCollage(teacher.getCollageId());
            deviceController.setCreator(user);
            deviceController.setCreateTime(new Date());
            deviceController.setLastModifiedUser(user);
            deviceController.setLastModifiedTime(new Date());
            Integer row = deviceMapper.addDeviceController(deviceController);
            if(row != 1){
                throw new InsertException("添加数据异常");
            }
            // 和远程终端交互
            /**
             *
             */
        }
        else{
            Device device = new Device();
            device.setSnId(deviceQo.getSnId());
            device.setName(deviceQo.getName());
            device.setCollageId(teacher.getCollageId());
            device.setPId(0);
            Integer row = deviceMapper.addDevice(device);
            if(row != 1){
                throw new InsertException("添加数据异常");
            }
        }
    }

    @Override
    public List<DeviceVo> allDevice(Integer user){
        Teacher teacher = teacherMapper.findById(user);
        Integer collage = teacher.getCollageId();
        List<DeviceVo> deviceVoList = new ArrayList<>();
        List<DeviceController> deviceControllerList = deviceMapper.getDeviceControllerByCollage(collage);
        for(DeviceController deviceController:deviceControllerList){
            DeviceVo deviceVo = new DeviceVo();
            deviceVo.setId(deviceController.getId());
            deviceVo.setType(0);
            deviceVo.setSnId(deviceController.getSnId());
            deviceVo.setName(deviceController.getName());
            deviceVo.setStatus(deviceController.getStatus());
            deviceVo.setVersion(deviceController.getVersion());
            List<Device> remoteDeviceList = deviceMapper.getDeviceByPid(deviceController.getId());
            deviceVo.setRemoteDevices(remoteDeviceList);
            deviceVoList.add(deviceVo);
        }
        List<Device> deviceList = deviceMapper.getDeviceByCollage(collage);
        for(Device device : deviceList){
            DeviceVo deviceVo = new DeviceVo();
            deviceVo.setId(device.getId());
            deviceVo.setType(1);
            deviceVo.setSnId(device.getSnId());
            deviceVo.setName(device.getName());
            deviceVoList.add(deviceVo);
        }
        return deviceVoList;
    }

    @Override
    public void addRemoteDevice(RemoteDeviceQo remoteDeviceQo, Integer user) {
        // 校验同一个控制器下的ip冲突
        Integer pId = remoteDeviceQo.getPId();
        String ip = remoteDeviceQo.getIp();
        Device existDevice = deviceMapper.getDeviceByPIdAndIp(pId,ip);
        if(existDevice!=null){
            throw new IpConflictException("远程控制器已关联该ip地址，请校验表单");
        }
        Teacher teacher = teacherMapper.findById(user);
        Integer collage = teacher.getCollageId();
        Device device = new Device();
        device.setSnId(remoteDeviceQo.getSnId());
        device.setName(remoteDeviceQo.getName());
        device.setCollageId(collage);
        device.setPId(remoteDeviceQo.getPId());
        device.setType(remoteDeviceQo.getType());
        device.setIp(remoteDeviceQo.getIp());
        device.setPort(remoteDeviceQo.getPort());
        device.setLiveUrl(remoteDeviceQo.getLiveUrl());
        device.setDeviceState(0);
        device.setRemoteControl(1); // 默认允许远程操作
        device.setCreator(user);
        device.setCreateTime(new Date());
        device.setLastModifiedTime(new Date());
        device.setLastModifiedUser(user);
        Integer res = deviceMapper.addDevice(device);
        if(res!=1){
            throw new InsertException("添加数据异常");
        }
        Integer frpServerId = device.getPId()/10000;
        Integer thisBoxPort = device.getPId()%10000;
        FrpServer thisFrpServer = deviceMapper.getFrpServerById(frpServerId);
        // 设置FRPServer端口管理  管理 remotePort  vncPort
        FrpServerPortManage frpServerPortManage = new FrpServerPortManage();
        frpServerPortManage.setDeviceId(device.getId());
        frpServerPortManage.setFrpServerId(device.getPId()/10000); // deviceManage 的ID 前三位
        //判断当前的frpServer 48000-48999 有哪些port没有用
        // todo
        frpServerPortManage.setRemotePort(48000+device.getId()); // 涉及重构
        if(device.getType().equals(2)){
            // 需要vncPort
            frpServerPortManage.setVncPort(frpServerPortManage.getRemotePort()+1000);
        }
        // 交互远程终端 做相应的操作
        TcpMessageDto tcpMessageDto = new TcpMessageDto();
        DeviceDetail deviceDetail = new DeviceDetail();
        tcpMessageDto.setType("add");
        deviceDetail.setId(device.getId());
        deviceDetail.setPid(0);
        deviceDetail.setFrpServerPort(thisFrpServer.getPort());
        deviceDetail.setFrpServerIp(thisFrpServer.getIp());
        deviceDetail.setLocalIp(device.getIp());
        deviceDetail.setLocalPort(device.getPort());
        deviceDetail.setRemotePort(frpServerPortManage.getRemotePort());
        tcpMessageDto.setDeviceDetail(deviceDetail);
        System.out.println(tcpMessageDto);
        String boxResponse = this.connectOnlineBox(thisFrpServer.getIp(), thisBoxPort,tcpMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许json使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            OnlineBoxResponse onlineBoxResponse = objectMapper.readValue(boxResponse, OnlineBoxResponse.class);
            if(onlineBoxResponse.getRes().equals(1)){
                deviceMapper.updateDeviceState(onlineBoxResponse.getDeviceId() ,1, onlineBoxResponse.getPid());
            }else{
                deviceMapper.delDevice(device.getId());
                throw new OnlineBoxHandleException("OnlineBox添加设备失败");
            }
        }catch (Exception e){
            deviceMapper.delDevice(device.getId());
            throw new ReceivedMessageException("解析OnlineBox数据异常: "+e.getMessage());
        }
        // 交互FRPServer  开启vnc服务
        if(device.getType().equals(2)){
            // 开启vnc 添加 device_frp_service_port_manage 的 数据
            VncResponse vncResponse = this.startVnc(thisFrpServer.getIp(), deviceDetail.getRemotePort(), deviceDetail.getRemotePort()+1000);
            if(!vncResponse.getRes().equals(1)){
                throw new FrpServiceHandleException("开启vnc远程服务失败");
            }
            frpServerPortManage.setVncPid(vncResponse.getData());
        }
        Integer re =  deviceMapper.addFrpServerPortManage(frpServerPortManage);
        if(re != 1){
            throw new InsertException("添加数据异常");
        }
        //  更新操作地址
        String remoteUrl = "?host="+thisFrpServer.getIp()+"&port="+frpServerPortManage.getRemotePort();
        if(device.getType().equals(2)){
            remoteUrl = "?host="+thisFrpServer.getIp()+"&port="+frpServerPortManage.getVncPort();
        }
        deviceMapper.updateRemoteUrl(device.getId(), remoteUrl);
    }

    @Override
    public List<DeviceType> allDeviceType() {
        List<DeviceType> deviceTypeList = deviceMapper.getAllDeviceType();
        return deviceTypeList;
    }

    @Override
    public void delRemoteDevice(Integer deviceId){
        Device existDevice = deviceMapper.getDeviceById(deviceId);
        FrpServerPortManage frpServerPortManage = deviceMapper.getFrpServerPortManageByDeviceId(deviceId);
        FrpServer frpServer = deviceMapper.getFrpServerById(existDevice.getPId()/10000);
        TcpMessageDto tcpMessageDto = new TcpMessageDto();
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setId(existDevice.getId());
        deviceDetail.setPid(existDevice.getBoxPid());
        deviceDetail.setFrpServerPort(frpServer.getPort());
        deviceDetail.setFrpServerIp(frpServer.getIp());
        deviceDetail.setLocalIp(existDevice.getIp());
        deviceDetail.setLocalPort(existDevice.getPort());
        deviceDetail.setRemotePort(frpServerPortManage.getRemotePort());
        tcpMessageDto.setDeviceDetail(deviceDetail);
        tcpMessageDto.setType("del");
        if(existDevice.getType().equals(2)){
            //todo 关闭vnc 删除 device_frp_service_port_manage 的 数据
            VncResponse vncResponse = this.stopVnc(frpServer.getIp(), frpServerPortManage.getVncPid());
            if(!vncResponse.getRes().equals(1)){
                throw new FrpServiceHandleException("关闭vnc远程服务失败");
            }
            Integer re = deviceMapper.deleteFrpPortManageByDeviceId(deviceId);
            if(re != 1 ){
                throw new DeleteException("删除端口数据异常");
            }
        }
        String boxResponse = this.connectOnlineBox(frpServer.getIp(), existDevice.getPId()%10000, tcpMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许json使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            OnlineBoxResponse onlineBoxResponse = objectMapper.readValue(boxResponse, OnlineBoxResponse.class);
            if(onlineBoxResponse.getRes().equals(1)){
                // 如果res=1 则执行成功
                Integer res = deviceMapper.delDevice(deviceId);
                if(res != 1){
                    throw new DeleteException("删除设备数据异常");
                }
            }else{
                throw new DeleteException("onlineBox停止服务异常");
            }
        }catch (JsonProcessingException e){
            e.printStackTrace();
            throw new ReceivedMessageException("解析OnlineBox数据异常"+ e.getMessage());
        }

    }

    @Override
    public Device changeRemoteControl(Integer id, Integer targetState, Integer user) {
        Device existDevice =  deviceMapper.getDeviceById(id);
        Integer currRemoteControl = existDevice.getRemoteControl();
        if(currRemoteControl.equals(targetState)){
            throw new SameStateException("目标状态和当前状态相同，无需更改");
        }
        // 发送开启/关闭远程操作指令
        if(targetState.equals(1)){
            Device device = this.startRemoteDeviceRemoteControl(existDevice,user);
            return device;
        } else if (targetState.equals(0)) {
            Device device = this.stopRemoteDeviceRemoteControl(existDevice, user);
            return device;
        }else {
            throw new UpdateException("错误的请求参数");
        }
    }

    @Override
    public Device refreshDeviceStatus(Integer id, Integer user){
        Device existDevice = deviceMapper.getDeviceById(id);
        FrpServerPortManage frpServerPortManage = deviceMapper.getFrpServerPortManageByDeviceId(id);
        FrpServer frpServer = deviceMapper.getFrpServerById(existDevice.getPId()/10000);
        TcpMessageDto tcpMessageDto = new TcpMessageDto();
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setId(existDevice.getId());
        deviceDetail.setPid(existDevice.getBoxPid());
        deviceDetail.setFrpServerPort(frpServer.getPort());
        deviceDetail.setFrpServerIp(frpServer.getIp());
        deviceDetail.setLocalIp(existDevice.getIp());
        deviceDetail.setLocalPort(existDevice.getPort());
        deviceDetail.setRemotePort(frpServerPortManage.getRemotePort());
        tcpMessageDto.setDeviceDetail(deviceDetail);
        tcpMessageDto.setType("status");
        String boxResponse = this.connectOnlineBox(frpServer.getIp(), existDevice.getPId()%10000, tcpMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许json使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            OnlineBoxResponse onlineBoxResponse = objectMapper.readValue(boxResponse, OnlineBoxResponse.class);
            if(onlineBoxResponse.getRes().equals(1)){
                Integer remoteControl = 1;
                if(onlineBoxResponse.getPid().equals(0)){
                    remoteControl = 0;
                }
                deviceMapper.updateRemoteControl(existDevice.getId(), remoteControl,
                        onlineBoxResponse.getDeviceState(), onlineBoxResponse.getPid(), user, new Date());
            }else {
                throw new OnlineBoxHandleException("OnlineBox处理失败: "+ onlineBoxResponse.getStrData());
            }
        }catch (JsonProcessingException e){
            throw new ReceivedMessageException("解析OnlineBox数据异常: "+ e.getMessage());
        }
        Device newDevice = deviceMapper.getDeviceById(existDevice.getId());
        return newDevice;
    }


    public Device startRemoteDeviceRemoteControl(Device existDevice, Integer user){
        FrpServer frpServer = deviceMapper.getFrpServerById(existDevice.getPId()/10000);
        FrpServerPortManage frpServerPortManage = deviceMapper.getFrpServerPortManageByDeviceId(existDevice.getId());
        TcpMessageDto tcpMessageDto = new TcpMessageDto();
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setId(existDevice.getId());
        deviceDetail.setPid(existDevice.getBoxPid());
        deviceDetail.setFrpServerPort(frpServer.getPort());
        deviceDetail.setFrpServerIp(frpServer.getIp());
        deviceDetail.setLocalIp(existDevice.getIp());
        deviceDetail.setLocalPort(existDevice.getPort());
        deviceDetail.setRemotePort(frpServerPortManage.getRemotePort());
        tcpMessageDto.setType("start");
        tcpMessageDto.setDeviceDetail(deviceDetail);
        String boxResponse = this.connectOnlineBox(frpServer.getIp(), existDevice.getPId()%10000, tcpMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许json使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            OnlineBoxResponse onlineBoxResponse = objectMapper.readValue(boxResponse, OnlineBoxResponse.class);
            if(onlineBoxResponse.getRes().equals(1)){
                deviceMapper.updateRemoteControl(existDevice.getId(), 1,
                        onlineBoxResponse.getDeviceState(), onlineBoxResponse.getPid(), user, new Date());
            }else {
                throw new OnlineBoxHandleException("OnlineBox处理失败: "+ onlineBoxResponse.getStrData());
            }
        }catch (JsonProcessingException e){
            throw new ReceivedMessageException("解析OnlineBox数据异常: "+ e.getMessage());
        }
        if(existDevice.getType().equals(2)){
            // 开启vnc 更新device_frp_service_port_manage 的 pid
            VncResponse vncResponse = this.startVnc(frpServer.getIp(), deviceDetail.getRemotePort(), deviceDetail.getRemotePort()+1000);
            if(!vncResponse.getRes().equals(1)){
                throw new FrpServiceHandleException("开启vnc远程服务失败");
            }
            frpServerPortManage.setVncPid(vncResponse.getData());
            deviceMapper.updateVncPid(frpServerPortManage);
        }

        // 更新操作地址
        String remoteUrl = "?host="+frpServer.getIp()+"&port="+frpServerPortManage.getRemotePort();
        if(existDevice.getType().equals(2)){
            remoteUrl = "?host="+frpServer.getIp()+"&port="+frpServerPortManage.getVncPort();
        }
        deviceMapper.updateRemoteUrl(existDevice.getId(), remoteUrl);
        Device newDevice = deviceMapper.getDeviceById(existDevice.getId());
        return newDevice;
    }

    public Device stopRemoteDeviceRemoteControl(Device existDevice, Integer user){
        FrpServer frpServer = deviceMapper.getFrpServerById(existDevice.getPId()/10000);
        FrpServerPortManage frpServerPortManage = deviceMapper.getFrpServerPortManageByDeviceId(existDevice.getId());
        TcpMessageDto tcpMessageDto = new TcpMessageDto();
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setId(existDevice.getId());
        deviceDetail.setPid(existDevice.getBoxPid());
        deviceDetail.setFrpServerPort(frpServer.getPort());
        deviceDetail.setFrpServerIp(frpServer.getIp());
        deviceDetail.setLocalIp(existDevice.getIp());
        deviceDetail.setLocalPort(existDevice.getPort());
        deviceDetail.setRemotePort(frpServerPortManage.getRemotePort());
        tcpMessageDto.setDeviceDetail(deviceDetail);
        tcpMessageDto.setType("stop");
        String boxResponse = this.connectOnlineBox(frpServer.getIp(), existDevice.getPId()%10000, tcpMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许json使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            OnlineBoxResponse onlineBoxResponse = objectMapper.readValue(boxResponse, OnlineBoxResponse.class);
            if(onlineBoxResponse.getRes().equals(1)){
                deviceMapper.updateRemoteControl(existDevice.getId(), 0,
                        onlineBoxResponse.getDeviceState(), onlineBoxResponse.getPid(), user, new Date());
            }else {
                throw new OnlineBoxHandleException("onlineBox处理失败："+ onlineBoxResponse.getStrData());
            }
        }catch (JsonProcessingException e){
            e.printStackTrace();
            throw new ReceivedMessageException("解析OnlineBox数据异常"+ e.getMessage());
        }
        if(existDevice.getType().equals(2)){
            //todo 关闭vnc 更新device_frp_service_port_manage 的 pid
            VncResponse vncResponse = this.stopVnc(frpServer.getIp(), frpServerPortManage.getVncPid());
            if(!vncResponse.getRes().equals(1)){
                throw new FrpServiceHandleException("关闭vnc远程服务失败");
            }
            frpServerPortManage.setVncPid(vncResponse.getData());
            deviceMapper.updateVncPid(frpServerPortManage);
        }

        // 更新操作地址 为空
        String remoteUrl = "";
        deviceMapper.updateRemoteUrl(existDevice.getId(), remoteUrl);
        Device newDevice = deviceMapper.getDeviceById(existDevice.getId());
        return newDevice;
    }


    /**
     * 连接onlineBox 并发送消息
     * @param ip frp server ip
     * @param port onlineBox  4900 穿透 对应的port  即id后4位
     * @param tcpMessageDto 发送给onlineBox的消息
     * @return
     */
    public String connectOnlineBox(String ip, Integer port, TcpMessageDto tcpMessageDto) {
        OnlineBoxTcpClient client = new OnlineBoxTcpClient();
        Gson gson = new Gson();
        final StringBuilder receivedMessage = new StringBuilder();
        // 设置消息监听器
        client.setMessageListener(message -> {
            // 处理接收到的消息
//            System.out.println("Message received in main program: " + message);
            receivedMessage.append(message);
        });

        // 建立连接发送消息
        client.connect(ip, port);
        client.sendMessage(gson.toJson(tcpMessageDto));
        for(int i=0;i<100; i++){
            if(receivedMessage.length()>0){
                break;
            }else {
                try{
                    if(i == 99){
                        throw new OnlineBoxResponseTimeoutException("OnlineBox 响应超时");
                    }
                    Thread.sleep(100);
                }catch (IllegalArgumentException | InterruptedException e){
                    throw new ReceivedMessageException(e.getMessage());
                }
            }
        }
        return receivedMessage.toString();
    }

    @Override
    public VncResponse startVnc(String ip, Integer vnc, Integer listen){
        // 创建 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();
        // 请求 URL
        String url = "http://"+ip+":8000/start";
        // 请求参数
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("vnc", vnc.toString());
        requestBody.add("listen", listen.toString());
        String response = restTemplate.postForObject(url, requestBody, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许json使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            VncResponse vncResponse = objectMapper.readValue(response, VncResponse.class);
            return vncResponse;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            throw new ReceivedMessageException("解析vncServer数据异常"+ e.getMessage());
        }
    }

    public VncResponse stopVnc(String ip, Integer pid){
        // 创建 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();
        // 请求 URL
        String url = "http://"+ip+":8000/stop";
        // 请求参数
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("pid", pid.toString());
        String response = restTemplate.postForObject(url, requestBody, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许json使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            VncResponse vncResponse = objectMapper.readValue(response, VncResponse.class);
            return vncResponse;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            throw new ReceivedMessageException("解析vncServer数据异常"+ e.getMessage());
        }
    }

    @Override
    public List<Device> deviceList(Integer user){
        List<Device> deviceList = deviceMapper.deviceList(user);
        return deviceList;
    }

    public String connectOnlineBoxTest(String ip, Integer port){
        TcpMessageDto tcpMessageDto = new TcpMessageDto();
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setLocalIp("192.168.1.41");
        deviceDetail.setLocalPort(5900);
        deviceDetail.setFrpServerPort(7000);
        deviceDetail.setFrpServerIp("47.108.137.115");
        deviceDetail.setId(1);
        deviceDetail.setRemotePort(48001);
        tcpMessageDto.setDeviceDetail(deviceDetail);
        tcpMessageDto.setType("add");
        String res = this.connectOnlineBox(ip, port, tcpMessageDto);
        ObjectMapper objectMapper = new ObjectMapper();
        //允许使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        try{
            OnlineBoxResponse onlineBoxResponse = objectMapper.readValue(res, OnlineBoxResponse.class);
            System.out.println(onlineBoxResponse);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            throw new ReceivedMessageException("解析OnlineBox数据异常"+ e.getMessage());
        }
        return res;
    }
}
