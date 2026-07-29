-- 任务模板关联所属导航地图，便于编辑时回填点位下拉
ALTER TABLE tcs_task_template
    ADD COLUMN navigation_map_id BIGINT NULL COMMENT '所属导航地图主键ID' AFTER name;
