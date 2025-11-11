delete from manager_info_table where manager_id = 1;

INSERT INTO manager_info_table (manager_id, manager_name, password, phone, wechat, create_time,update_time)
VALUES (1, 'admin', 'admin','17624224680','11111111111',FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss'),FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss'))