package com.iecube.community.model.remote_project.controller;

import com.iecube.community.basecontroller.remote_project.RemoteProjectBaseController;
import com.iecube.community.model.remote_appointment.entity.RemoteAppointment;
import com.iecube.community.model.remote_appointment.qo.RemoteAppointmentQo;
import com.iecube.community.model.remote_appointment.service.RemoteAppointmentService;
import com.iecube.community.model.remote_appointment.vo.RemoteAppointmentVo;
import com.iecube.community.model.remote_project.service.RemoteProjectService;
import com.iecube.community.model.remote_project.vo.RemoteOperationVo;
import com.iecube.community.model.remote_project.vo.RemoteProjectVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/remote")
public class RemoteProjectController extends RemoteProjectBaseController {

    @Autowired
    private RemoteProjectService remoteProjectService;

    @Autowired
    private RemoteAppointmentService remoteAppointmentService;


    @GetMapping("/{projectId}")
    public JsonResult<RemoteProjectVo> getRemoteProjectVo(@PathVariable Integer projectId){
        RemoteProjectVo remoteProjectVo = remoteProjectService.studentGetRemoteProject(projectId);
        return new JsonResult<>(OK, remoteProjectVo);
    }

    @GetMapping("/{projectId}/my_appointment")
    public JsonResult<List> getStudentAppointment(@PathVariable Integer projectId){
        Integer user = currentUserId();
        List<RemoteAppointmentVo> remoteAppointmentVoList = remoteAppointmentService.studentAppointmentList(projectId, user);
        return new JsonResult<>(OK, remoteAppointmentVoList);
    }

    @PostMapping("/appointment/query")
    public JsonResult<List> getAppointmentList(@RequestBody RemoteAppointmentQo remoteAppointmentQo){
        List<RemoteAppointment> remoteAppointmentList = remoteAppointmentService.getAppointmentList(remoteAppointmentQo);
        return new JsonResult<>(OK, remoteAppointmentList);
    }

    @PostMapping("/appointment")
    public JsonResult<Void> studentAppointment(Integer appointmentId){
        Integer user = currentUserId();
        remoteAppointmentService.studentAppointment(appointmentId, user);
        return new JsonResult<>(OK);
    }

    @PostMapping("/appointment/cancel")
    public JsonResult<Void> studentCancelAppointment(Integer appointmentId){
        Integer user = currentUserId();
        remoteAppointmentService.studentCancelAppointment(appointmentId, user);
        return new JsonResult<>(OK);
    }

    @PostMapping("/operation/{appointmentId}")
    public JsonResult<RemoteOperationVo> remoteOperation(@PathVariable Integer appointmentId){
        Integer user = currentUserId();
        RemoteOperationVo remoteOperationVo = remoteProjectService.remoteOperation(appointmentId, user);
        return new JsonResult<>(OK, remoteOperationVo);
    }
}
