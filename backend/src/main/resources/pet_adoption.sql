-- ============================================================
-- 宠物领养平台 - MySQL 8.x 初始化脚本
-- 库名：pet_adoption
-- 字符集：utf8mb4
-- 图片路径：相对路径 /pet-images/...（由前端 Vite 代理转发到 MinIO）
-- ============================================================
SET NAMES utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

CREATE DATABASE IF NOT EXISTS pet_adoption
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pet_adoption;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(40)  NOT NULL UNIQUE COMMENT '登录名',
    password    VARCHAR(255) NOT NULL COMMENT 'BCrypt 密文或 {noop} 前缀',
    nickname    VARCHAR(40),
    phone       VARCHAR(20),
    email       VARCHAR(80),
    role        VARCHAR(20)  DEFAULT 'USER' COMMENT 'ADMIN / USER',
    avatar      VARCHAR(500),
    disabled    BIT          NOT NULL DEFAULT b'0',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_role (role),
    INDEX idx_user_disabled (disabled)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '用户表';

-- ----------------------------
-- 宠物表
-- ----------------------------
DROP TABLE IF EXISTS pets;
CREATE TABLE pets
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    type              VARCHAR(40) NOT NULL COMMENT '狗/猫/兔/仓鼠',
    name              VARCHAR(80) NOT NULL,
    brief             VARCHAR(220),
    description       TEXT,
    habit             TEXT,
    health            TEXT,
    height            DOUBLE,
    weight            DOUBLE,
    gender            VARCHAR(10) COMMENT '公/母',
    age               INT,
    age_unit          VARCHAR(10) DEFAULT '岁',
    body_size         VARCHAR(20) COMMENT '小型/中型/大型',
    sterilized        BIT,
    main_image        VARCHAR(500),
    detail_images     TEXT COMMENT 'JSON 数组字符串',
    requirements      TEXT,
    status            VARCHAR(20) DEFAULT 'available' COMMENT 'available/adopted/offline',
    view_count        INT         DEFAULT 0,
    application_count INT         DEFAULT 0,
    create_time       DATETIME    DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_pet_type (type),
    INDEX idx_pet_status (status),
    INDEX idx_pet_create_time (create_time),
    INDEX idx_pet_hot (status, view_count, application_count)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '宠物表';

-- ----------------------------
-- 领养申请表
-- ----------------------------
DROP TABLE IF EXISTS adoptions;
CREATE TABLE adoptions
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id        BIGINT,
    pet_id         BIGINT,
    pet_name       VARCHAR(80),
    pet_type       VARCHAR(40),
    pet_image      VARCHAR(500),
    applicant_name VARCHAR(40),
    phone          VARCHAR(20),
    housing        VARCHAR(120),
    experience     VARCHAR(200),
    reason         TEXT,
    status         VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/approved/rejected/canceled',
    apply_time     DATETIME    DEFAULT CURRENT_TIMESTAMP,
    review_time    DATETIME,
    review_comment VARCHAR(300),
    INDEX idx_adoption_user (user_id),
    INDEX idx_adoption_pet (pet_id),
    INDEX idx_adoption_status (status),
    INDEX idx_adoption_apply_time (apply_time),
    INDEX idx_adoption_status_time (status, apply_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '领养申请表';

-- ----------------------------
-- 初始数据
-- admin / user / student 密码均为 {noop}123456
-- 图片路径使用相对路径，例如 /pet-images/dog/dog1.png
-- ----------------------------
INSERT INTO users(username, password, nickname, phone, email, role, avatar, disabled, create_time)
VALUES ('admin', '{noop}123456', '管理员', '13800000000', 'admin@example.com', 'ADMIN',
        '/pet-images/admin.jpg', b'0', NOW()),
       ('user', '{noop}123456', '普通用户', '13900000000', 'user@example.com', 'USER',
        '/pet-images/common_user.png', b'0', NOW()),
       ('student', '{noop}123456', '学生用户', '13700000000', 'student@example.com', 'USER',
        '/pet-images/common_user.png', b'0', NOW());

-- 6 只宠物，分别对应 6 个文件夹
INSERT INTO pets(type, name, brief, description, habit, health, height, weight, gender, age, age_unit, body_size,
                 sterilized, main_image, detail_images, requirements, status, view_count, application_count, create_time)
VALUES
-- 1. 奶糖 - 猫（cat/）
('猫', '奶糖', '温柔亲人的小猫', '奶糖刚见面会有一点害羞，熟悉以后会主动蹭人。适合安静陪伴的领养人。',
 '喜欢晒太阳和安静陪伴，晚上喜欢窝在人腿边。', '已完成基础体检和驱虫，食欲正常，精神状态良好。',
 27, 4.2, '母', 2, '岁', '中型', b'1',
 '/pet-images/cat/cat1.png',
 '["/pet-images/cat/cat1.png","/pet-images/cat/cat2.png","/pet-images/cat/cat3.png"]',
 '有稳定住所，愿意科学喂养，接受回访，不笼养。', 'available', 38, 2, DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- 2. 小橙 - 狗（dog/）
('狗', '小橙', '活泼亲人的小狗', '小橙喜欢散步和追球，对人很友好。适合愿意每天陪它活动的领养人。',
 '喜欢散步、追球和互动，精力比较旺盛。', '疫苗齐全，体检正常，已做基础驱虫。',
 42, 9.8, '公', 1, '岁', '中型', b'1',
 '/pet-images/dog/dog1.png',
 '["/pet-images/dog/dog1.png","/pet-images/dog/dog2.png","/pet-images/dog/dog3.png"]',
 '每天有固定遛狗时间，不长期笼养，出门牵绳。', 'available', 57, 3, DATE_SUB(NOW(), INTERVAL 10 DAY)),

-- 3. 多多 - 狗（sample-dog/）
('狗', '多多', '性格温顺亲人，已经完成基础训练，适合有庭院的家庭。',
 '多多是一只非常聪明的中型犬，活泼但不闹腾。已经学会坐下、握手等基础指令，对人友善，尤其喜欢和小朋友玩耍。',
 '每天需要至少两次户外散步（每次约30分钟），喜欢玩飞盘和球类，不挑食。',
 '已绝育，疫苗齐全，定期内外驱虫，骨骼发育良好，无遗传疾病。',
 52, 18.5, '公', 2, '岁', '中型', b'1',
 '/pet-images/sample-dog/sample-dog.png',
 '["/pet-images/sample-dog/sample-dog.png","/pet-images/sample-dog/sample-dog-2.png","/pet-images/sample-dog/sample-dog-3.png"]',
 '有稳定居住环境（非宿舍），能接受定期视频回访，承诺科学喂养、出门牵绳。', 'available', 5, 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- 4. 团子 - 兔（rabbit/）
('兔', '团子', '干净安静的小兔子', '团子适应环境比较快，喜欢干净安静的空间，性格温和。',
 '喜欢提摩西草和安静角落，白天多休息，傍晚比较活跃。', '精神状态良好，进食正常，毛发顺滑。',
 18, 1.6, '母', 8, '月', '小型', b'0',
 '/pet-images/rabbit/rabbit1.png',
 '["/pet-images/rabbit/rabbit1.png","/pet-images/rabbit/rabbit2.png","/pet-images/rabbit/rabbit3.png","/pet-images/rabbit/rabbit4.png","/pet-images/rabbit/rabbit5.png"]',
 '准备兔笼、干草和饮水器，保持笼具清洁，不喂人类食物。', 'available', 21, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- 5. 芝麻 - 仓鼠（hamster/）
('仓鼠', '芝麻', '小巧可爱的仓鼠', '芝麻夜间比较活跃，喜欢跑轮和磨牙玩具，体型小巧适合新手。',
 '夜间活跃，喜欢探索和跑轮，有藏食习惯。', '毛发顺滑，进食正常，牙齿健康。',
 8, 0.15, '公', 5, '月', '小型', b'0',
 '/pet-images/hamster/hamster1.png',
 '["/pet-images/hamster/hamster1.png","/pet-images/hamster/hamster2.webp","/pet-images/hamster/hamster3.webp","/pet-images/hamster/hamster4.webp","/pet-images/hamster/hamster5.webp","/pet-images/hamster/hamster6.webp","/pet-images/hamster/hamster7.webp"]',
 '单独饲养，保持笼具清洁，准备跑轮和磨牙石。', 'available', 18, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- 6. 豆包 - 仓鼠（sample-hamster/）
('仓鼠', '豆包', '夜间活跃、喜欢跑轮和藏食，体型小巧适合新手。',
 '豆包是一只精力充沛的小仓鼠，夜间活动较多。喜欢在跑轮上运动，并且有藏食的小习惯。',
 '夜间活跃，喜欢跑轮和藏食，需要安静通风的环境。喜欢啃咬磨牙石，不爱被强行抱起。',
 '精神饱满，食欲正常，无皮肤问题，牙齿健康。',
 8, 0.15, '公', 1, '月', '小型', b'0',
 '/pet-images/sample-hamster/sample-hamster.png',
 '["/pet-images/sample-hamster/sample-hamster.png","/pet-images/sample-hamster/sample-hamster-2.png","/pet-images/sample-hamster/sample-hamster-3.png"]',
 '准备好足够大的笼具、静音跑轮、安全垫料以及充足的鼠粮，能接受定期回访。', 'available', 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ----------------------------
-- 领养申请示例
-- ----------------------------
INSERT INTO adoptions(user_id, pet_id, pet_name, pet_type, pet_image, applicant_name, phone, housing, experience,
                      reason, status, apply_time, review_time, review_comment)
VALUES
    (2, 1, '奶糖', '猫', '/pet-images/cat/cat1.png',
     '普通用户', '13900000000', '整租住房', '有养猫经验', '我会给奶糖稳定的生活环境。',
     'approved', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), '审核通过，请保持电话畅通'),

    (2, 2, '小橙', '狗', '/pet-images/dog/dog1.png',
     '普通用户', '13900000000', '自有住房', '养过狗', '每天可以遛狗两次，希望领养小橙。',
     'rejected', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), '资料暂不完整，可补充后再次申请'),

    (3, 4, '团子', '兔', '/pet-images/rabbit/rabbit1.png',
     '学生用户', '13700000000', '宿舍', '第一次养兔', '已经准备好兔笼和干草。',
     'rejected', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), '宿舍环境不适合'),

    (3, 5, '芝麻', '仓鼠', '/pet-images/hamster/hamster1.png',
     '学生用户', '13700000000', '整租住房', '第一次养小宠', '已准备独立笼具。',
     'pending', DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, NULL),

    (2, 6, '豆包', '仓鼠', '/pet-images/sample-hamster/sample-hamster.png',
     '普通用户', '13900000000', '自有住房', '第一次养仓鼠', '希望给豆包一个温暖的家。',
     'pending', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL);