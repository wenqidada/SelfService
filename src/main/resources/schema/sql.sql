CREATE TABLE IF NOT EXISTS details_table (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    user_info VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    open_type tinyint(1),
    table_number tinyint(2),
    coupon_code VARCHAR(255),
    money DECIMAL(4, 2)
    );

COMMENT ON COLUMN details_table.id IS '编号';
COMMENT ON COLUMN details_table.user_info IS '用户信息';
COMMENT ON COLUMN details_table.start_time IS '开台时间';
COMMENT ON COLUMN details_table.end_time IS '结束时间';
COMMENT ON COLUMN details_table.open_type IS '开台类型,1会员2押金3定时4抖音5美团';
COMMENT ON COLUMN details_table.table_number IS '台号';
COMMENT ON COLUMN details_table.coupon_code IS '劵码,无劵码为0';
COMMENT ON COLUMN details_table.money IS '消费金额';

CREATE TABLE IF NOT EXISTS billiard_table (
    table_number INT(2) AUTO_INCREMENT PRIMARY KEY,
    lock_no VARCHAR(255),
    QR_code VARCHAR(255),
    table_info VARCHAR(255),
    table_type tinyint(1),
    use_type tinyint(1),
    cost DECIMAL(2, 2),
    update_user tinyint(2),
    update_time TIMESTAMP
    );

COMMENT ON COLUMN billiard_table.table_number IS '台号';
COMMENT ON COLUMN billiard_table.lock_no IS '锁编号';
COMMENT ON COLUMN billiard_table.QR_code IS '二维码链接';
COMMENT ON COLUMN billiard_table.table_info IS '球桌信息';
COMMENT ON COLUMN billiard_table.table_type IS '台子类型,1台球桌2棋牌室3存杆柜';
COMMENT ON COLUMN billiard_table.use_type IS '0正在使用,1未使用';
COMMENT ON COLUMN billiard_table.cost IS '费用/小时';
COMMENT ON COLUMN billiard_table.update_user IS '修改人';
COMMENT ON COLUMN billiard_table.update_time IS '修改时间';



CREATE TABLE IF NOT EXISTS vip_info_table (
    vip_id INT(10) AUTO_INCREMENT PRIMARY KEY,
    vip_user VARCHAR(255),
    balance DECIMAL(7, 2),
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );


COMMENT ON COLUMN vip_info_table.vip_id IS '会员编号';
COMMENT ON COLUMN vip_info_table.vip_user IS '用户信息';
COMMENT ON COLUMN vip_info_table.balance IS '余额';
COMMENT ON COLUMN vip_info_table.create_time IS '创建时间';
COMMENT ON COLUMN vip_info_table.update_time IS '更新时间';


CREATE TABLE IF NOT EXISTS deposit_table (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    deposit_id INT(2),
    deposit_user VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP
    );


COMMENT ON COLUMN deposit_table.id IS '存杆柜记录编号';
COMMENT ON COLUMN deposit_table.deposit_id IS '存杆柜编号';
COMMENT ON COLUMN deposit_table.deposit_user IS '用户信息';
COMMENT ON COLUMN deposit_table.start_time IS '存放开始时间';
COMMENT ON COLUMN deposit_table.end_time IS '结束时间';

CREATE TABLE IF NOT EXISTS manager_info_table (
    manager_id INT(2) AUTO_INCREMENT PRIMARY KEY,
    manager_name VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255),
    wechat VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );

COMMENT ON COLUMN manager_info_table.manager_id IS '管理员编号';
COMMENT ON COLUMN manager_info_table.manager_name IS '管理员姓名';
COMMENT ON COLUMN manager_info_table.password IS '密码';
COMMENT ON COLUMN manager_info_table.phone IS '手机号';
COMMENT ON COLUMN manager_info_table.wechat IS '微信号';
COMMENT ON COLUMN manager_info_table.create_time IS '创建时间';
COMMENT ON COLUMN manager_info_table.update_time IS '修改时间';

CREATE TABLE IF NOT EXISTS wechat_table (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(255),
    session_key VARCHAR(255),
    token VARCHAR(255),
    login_time TIMESTAMP,
    create_time TIMESTAMP
    );

COMMENT ON COLUMN wechat_table.id IS '主键ID';
COMMENT ON COLUMN wechat_table.open_id IS '用户唯一标识(小程序独立)';
COMMENT ON COLUMN wechat_table.session_key IS 'session_key';
COMMENT ON COLUMN wechat_table.login_time IS '登陆时间';
COMMENT ON COLUMN wechat_table.token IS '会话token';
COMMENT ON COLUMN wechat_table.create_time IS '创建时间';



CREATE TABLE IF NOT EXISTS tripartite_table (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    platform_type tinyint(1),
    codes VARCHAR(255),
    encrypted_codes VARCHAR(255),
    request_id VARCHAR(255),
    actual_payment DECIMAL(4, 2),
    amount DECIMAL(4, 2),
    create_time TIMESTAMP
    );

COMMENT ON COLUMN tripartite_table.id IS '主键Id';
COMMENT ON COLUMN tripartite_table.platform_type IS '类型:1抖音2美团3其它';
COMMENT ON COLUMN tripartite_table.codes IS '券码';
COMMENT ON COLUMN tripartite_table.encrypted_codes IS '加密券码';
COMMENT ON COLUMN tripartite_table.request_id IS '请求Id';
COMMENT ON COLUMN tripartite_table.actual_payment IS '实付金额';
COMMENT ON COLUMN tripartite_table.amount IS '订单金额';
COMMENT ON COLUMN tripartite_table.create_time IS '创建时间';
