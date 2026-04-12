package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.kernel.api.dto.VehicleDTO;
import org.opentcs.vehicle.application.VehicleApplicationService;
import org.opentcs.vehicle.application.bo.VehicleBO;
import org.opentcs.vehicle.application.bo.VehicleCrudBO;
import org.opentcs.vehicle.controller.req.RegisterVehicleWithDriverRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 车辆管理
 */
@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController extends BaseController {

    private final VehicleApplicationService vehicleApplicationService;

    @GetMapping("/list")
    public TableDataInfo<VehicleCrudBO> listVehicles(VehicleBO vehicle, PageQuery pageQuery) {
        return vehicleApplicationService.listVehicles(vehicle, pageQuery);
    }

    @GetMapping("/getAll")
    public R<List<VehicleCrudBO>> getAllVehicles() {
        return R.ok(vehicleApplicationService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public R<VehicleCrudBO> getVehicleById(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.getVehicleCrudById(id));
    }

    @PostMapping("/create")
    public R<Boolean> createVehicle(@RequestBody VehicleBO vehicle) {
        return R.ok(vehicleApplicationService.createVehicle(vehicle));
    }

    @PutMapping("/update")
    public R<Boolean> updateVehicle(@RequestBody VehicleBO vehicle) {
        return R.ok(vehicleApplicationService.updateVehicle(vehicle));
    }

    @DeleteMapping("/delete/{id}")
    public R<Boolean> deleteVehicle(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.deleteVehicle(id));
    }

    @GetMapping("/runtime/status/{name}")
    public R<VehicleDTO> getVehicleRuntimeStatus(@PathVariable String name) {
        return R.ok(vehicleApplicationService.getVehicleRuntimeStatus(name));
    }

    @GetMapping("/runtime/status/all")
    public R<List<VehicleDTO>> getAllVehicleRuntimeStatus() {
        return R.ok(vehicleApplicationService.getAllVehicleRuntimeStatus());
    }

    @GetMapping("/runtime/available")
    public R<List<VehicleDTO>> getAvailableVehicles() {
        return R.ok(vehicleApplicationService.getAvailableVehicles());
    }

    @GetMapping("/status/{id}")
    public R<VehicleBO> getVehicleStatus(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.getVehicleStatus(id));
    }

    @GetMapping("/status/all")
    public R<List<VehicleBO>> getAllVehicleStatus() {
        return R.ok(vehicleApplicationService.getAllVehicleStatus());
    }

    @PostMapping("/control/{id}")
    public R<String> controlVehicle(@PathVariable Long id, @RequestParam String command,
                                    @RequestBody Map<String, Object> params) {
        return R.ok();
    }

    @PostMapping("/instantAction/{name}")
    public R<Boolean> sendInstantAction(@PathVariable String name, @RequestBody InstantAction action) {
        vehicleApplicationService.sendInstantAction(name, action);
        return R.ok(true);
    }

    @GetMapping("/statistics")
    public R<Map<String, Object>> getVehicleStatistics() {
        return R.ok(vehicleApplicationService.getVehicleStatistics());
    }

    @GetMapping("/history/{id}")
    public TableDataInfo<Map<String, Object>> getVehicleHistory(@PathVariable Long id, PageQuery pageQuery) {
        return vehicleApplicationService.getVehicleHistory(id, pageQuery);
    }

    @PostMapping("/register")
    public R<Boolean> registerVehicle(@RequestBody VehicleBO vehicle) {
        return R.ok(vehicleApplicationService.registerVehicle(vehicle, null));
    }

    @PostMapping("/registerWithDriver")
    public R<Boolean> registerVehicleWithDriver(@RequestBody RegisterVehicleWithDriverRequest request) {
        return R.ok(vehicleApplicationService.registerVehicle(request.getVehicle(), request.getDriverConfig()));
    }

    @PostMapping("/activate/{id}")
    public R<Boolean> activateVehicle(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.activateVehicle(id));
    }

    @PostMapping("/deactivate/{id}")
    public R<Boolean> deactivateVehicle(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.deactivateVehicle(id));
    }

    @PostMapping("/unregister/{id}")
    public R<Boolean> unregisterVehicle(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.unregisterVehicle(id));
    }

}
