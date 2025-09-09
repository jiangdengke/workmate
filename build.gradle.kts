// 让 Kotlin DSL 识别 Spring Boot 的 BootJar 任务类型
import org.springframework.boot.gradle.tasks.bundling.BootJar

// ------ 统一管理一些三方库版本（可在 dependencies 中引用变量） ------
val jooqVersion by extra("3.19.24")
val testcontainersVersion by extra("1.21.3")

plugins {
    // 基础 Java 插件
    java
    `java-library`
    // 代码覆盖率统计
    jacoco
    // Spring Boot 核心插件（提供 bootRun、bootJar 等）
    id("org.springframework.boot") version "3.3.9"
    // Spring 依赖管理（自动引入 Spring 依赖版本对齐）
    id("io.spring.dependency-management") version "1.1.7"
    // 生成 OpenAPI 文档的 Gradle 插件（配合 springdoc 使用）
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    // 静态代码检查（PMD）
    id("pmd")
    // jOOQ 代码生成插件（基于 SQL/数据库模式生成类型安全的 DSL/DAO）
    id("org.jooq.jooq-codegen-gradle") version "3.19.24"
    // 代码格式化（Spotless）
    id("com.diffplug.spotless") version "7.1.0"
}

// ------ 将 jOOQ 生成的源码目录加入到 main/test 的源码路径中 ------
// 这样生成后的代码可被编译与在测试中引用
sourceSets {
    main {
        java {
            srcDir("build/generated-sources/jooq")
        }
    }
    test {
        java {
            srcDir("build/generated-sources/jooq")
        }
    }
}

// ------ 工程基础信息 ------
group = "org.jdk.mjga"
version = "1.0.0"
description = "make java great again!"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    // compileOnly 也能使用注解处理器（如 Lombok、配置处理器）
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    // 使用 Maven Central 仓库
    mavenCentral()
}

dependencies {
    // ===== Spring Boot 核心能力 =====
    implementation("org.springframework.boot:spring-boot-starter-actuator") // 健康检查、指标等
    implementation("org.springframework.boot:spring-boot-starter-jooq") // jOOQ 与 Spring 集成
    implementation("org.springframework.boot:spring-boot-starter-validation") // Bean 校验（Jakarta Validation）
    implementation("org.springframework.boot:spring-boot-starter-web") // Web MVC（JSON/REST）
    implementation("org.springframework.boot:spring-boot-starter-aop") // AOP（切面/拦截）

    // ===== 常用工具库 =====
    implementation("org.apache.commons:commons-lang3:3.17.0") // 字符串/对象工具
    implementation("org.apache.commons:commons-collections4:4.4") // 集合扩展

    // ===== OpenAPI / Swagger UI（基于 springdoc）=====
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.auth0:java-jwt:4.4.0")

    // ===== jOOQ 运行/元数据依赖 =====
    implementation("org.jooq:jooq-meta:$jooqVersion") // 读取数据库元数据/DDL

    // ===== 测试容器（集成测试用 Docker 化依赖，如 PostgreSQL）=====
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:mysql:$testcontainersVersion")
    // 注意：BOM 通常建议用 platform() 引入，这里保持你的写法
    testImplementation("org.testcontainers:testcontainers-bom:$testcontainersVersion")

    // ===== JDBC 驱动 =====
    runtimeOnly("com.mysql:mysql-connector-j") // 生产/运行期 PostgreSQL 驱动

    // ===== Lombok / 注解处理器 =====
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor") // 生成配置元数据（IDE 友好提示）
    annotationProcessor("org.projectlombok:lombok")

    // ===== 开发期热部署 =====
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ===== 测试依赖 =====
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux") // WebTestClient 等
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test") // 若涉及安全测试可用
    // 邮件
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // ===== jOOQ 代码生成期依赖（用于 codegen 任务，不参与运行期）=====
    jooqCodegen("org.jooq:jooq-codegen:$jooqVersion") // jOOQ 代码生成器
    jooqCodegen("org.jooq:jooq-meta-extensions:$jooqVersion") // 扩展（如 DDLDatabase）

    // ===== 可选：JSR 注解（空值标注）=====
    api("org.jspecify:jspecify:1.0.0")
}

// 打包为 app.jar，便于容器/服务器统一命名
tasks.withType<BootJar> {
    archiveFileName.set("app.jar")
}

// 使用 JUnit 5（JUnit Platform）
tasks.withType<Test> {
    useJUnitPlatform()
}

// 测试后自动生成 Jacoco 报告
tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

// Jacoco 覆盖率报告配置
tasks.jacocoTestReport {
    dependsOn(tasks.test) // 生成报告前先运行测试
}

jacoco {
    toolVersion = "0.8.13"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

// PMD 静态检查配置
pmd {
    // 仅检查 main 源集
    sourceSets = listOf(java.sourceSets.findByName("main"))
    isConsoleOutput = true
    toolVersion = "7.15.0"
    rulesMinimumPriority.set(5) // 只报告优先级 1..5 中较高（数值小越高）的规则
    ruleSetFiles = files("pmd-rules.xml") // 自定义规则文件（项目根目录）
}

// Spotless 代码格式化配置
spotless {
    // 针对杂项文件（根目录）的格式化
    format("misc") {
        target("*.gradle.kts", "*.md", ".gitignore")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }

    // Java 源码格式化：Google Java Format
    java {
        googleJavaFormat("1.28.0").reflowLongStrings() // 长字符串自动换行
        formatAnnotations() // 统一注解样式
    }

    // 针对 Gradle Kotlin 脚本的格式化
    kotlinGradle {
        target("*.gradle.kts")
        ktlint() // 使用 ktlint 规则
    }
}

// jOOQ 代码生成配置：基于“SQL 文件（无需连接数据库）”生成
jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/db/*.sql"
                    }
                    property {
                        key = "dialect"
                        value = "MYSQL"
                    } // 告诉解析器按 MySQL 语法读
                    property {
                        key = "sort"
                        value = "semantic"
                    }
                    property {
                        key = "unqualifiedSchema"
                        value = "none"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                }

                isUnsignedTypes = false // 在 DDLDatabase 下不总是生效，但留着无妨

                // ✅ 关键：用列名匹配强制类型（比按“原始类型字符串”更可靠）
                forcedTypes {
                    // 所有主键 id（根据你的三张表逐列列出或用正则）
                    forcedType {
                        name = "BIGINT" // 生成成 Java Long
                        includeExpression = "(?i).*\\.(users|notes|todos|reminders)\\.id|.*\\.(user_id)"
                    }
                    // 其他容易被识别错/降级的类型也可以按需强制（可选）
                    forcedType {
                        name = "TIMESTAMP" // 对应 DATETIME(3)
                        includeExpression = "(?i).*\\.(created_at|done_at|at_time)"
                    }
                    forcedType {
                        name = "BOOLEAN"
                        includeExpression = "(?i).*\\.(done|sent)"
                    }
                    // MySQL TEXT → 建议映射为 CLOB 或 LONGVARCHAR
                    forcedType {
                        name = "CLOB"
                        includeExpression = "(?i).*\\.(title|text)"
                    }
                    forcedType {
                        name = "VARCHAR"
                        includeExpression = "(?i).*\\.(user_name|email|display_name|password)"
                    }
                }
            }
            target {
                packageName = "org.jooq.generated"
                directory = "build/generated-sources/jooq"
            }
            generate {
                isDaos = true
                isRecords = true
                isSpringDao = true
                isSpringAnnotations = true
                isFluentSetters = true
                isDeprecated = false
            }
        }
    }
}
