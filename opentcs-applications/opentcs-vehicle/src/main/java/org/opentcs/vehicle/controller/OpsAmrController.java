package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.vehicle.application.VehicleApplicationService;
import org.opentcs.vehicle.application.bo.OpsActionResultBO;
import org.opentcs.vehicle.controller.req.GoChargeRequest;
import org.opentcs.vehicle.controller.req.MapSwitchRequest;
import org.opentcs.vehicle.controller.req.ModeSwitchRequest;
import org.opentcs.vehicle.controller.req.MoveRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ops/amr")
@RequiredArgsConstructor
public class OpsAmrController {

    private final VehicleApplicationService vehicleApplicationService;

    @PostMapping("/{vehicleName}/mode/switch")
    public R<OpsActionResultBO> switchMode(@PathVariable String vehicleName, @RequestBody ModeSwitchRequest request) {
        return R.ok(vehicleApplicationService.switchMode(vehicleName, request));
    }

    @PostMapping("/{vehicleName}/map/switch")
    public R<OpsActionResultBO> switchMap(@PathVariable String vehicleName, @RequestBody MapSwitchRequest request) {
        return R.ok(vehicleApplicationService.switchMap(vehicleName, request));
    }

    @PostMapping("/{vehicleName}/charge/go")
    public R<OpsActionResultBO> goCharge(@PathVariable String vehicleName, @RequestBody GoChargeRequest request) {
        return R.ok(vehicleApplicationService.goCharge(vehicleName, request));
    }

    @PostMapping("/{vehicleName}/move")
    public R<OpsActionResultBO> moveVehicle(@PathVariable String vehicleName, @RequestBody MoveRequest request) {
        return R.ok(vehicleApplicationService.moveVehicle(vehicleName, request));
    }

    @GetMapping("/{vehicleName}/precheck")
    public R<Map<String, Object>> precheck(@PathVariable String vehicleName, @RequestParam String actionType) {
        return R.ok(vehicleApplicationService.precheck(vehicleName, actionType));
    }

    @GetMapping("/actions")
    public R<List<Map<String, Object>>> listActionRecords(@RequestParam(required = false) String vehicleName) {
        return R.ok(vehicleApplicationService.listOpsActionRecords(vehicleName));
    }
}
