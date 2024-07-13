# 1. 개발환경 및 실행환경
+ 개발환경
    1. IDE : Visual Studio Code
    2. OS : Mac OS (arm64)
    3. JDK : openjdk version "22.0.1" 2024-04-16
+ API 테스트
    + 첨부된 ContactRestApiPostMan.json 을 Postman에 Import 하고 input.json, input_scv 파일을 사용하여 테스트

+ 실행환경
    1. 실행변수 : --spring.profiles.active=prod
    2. launch.json
    ```json
    {
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Current File",
            "request": "launch",
            "mainClass": "${file}"
        },
        {
            "type": "java",
            "name": "ContactApplication",
            "request": "launch",
            "mainClass": "com.task20240708.contact.ContactApplication",
            "projectName": "contact",
            "args": "--spring.profiles.active=prod" //active profile 설정
        }
        ]
    }
    ```

# 2. 요구사항
+ 직원의 기본 연락 정보를 확인 할 수 있어야 한다
+ 직원 정보는 csv, Json 형태로 제공된다
## 파일 형식
### Json
```json
[
    {
        "name":"이무기",
        "email":"weapon@clivf.com",
        "tel":"010-1111-2424",
        "joined":"2020-01-05"
    },
    {
        "name":"판브이",
        "email":"panv@clivf.com",
        "tel":"010-3535-7979",
        "joined":"2013-07-01"
    },
    {
        "name":"차호빵",
        "email":"hobread@clivf.com",
        "tel":"010-8531-7942",
        "joined":"2019-12-05"
    }
]
```
### csv
```csv
김철수,charles@clovf.com,01075312468,2018.03.07
박영희,charles@clovf.com,01012345678,2021.04.28
홍길동,charles@clovf.com,01045677890,2018.03.07
```
## 구현 API
1. 전체조회 : http://localhost:8080/api/v1/members
2. 단일조회 : http://localhost:8080/api/v1/member/{이름}
3. 사용자 추가 : http://localhost:8080/api/v1/members

# 3. 요구사항 분석
## 1. 전체조회
단순하게 Member Entity를 전체조회 하는 JPA 함수를 리턴 한다.
## 2. 단일조회
이름으로 조회를 하기 때문에 동명이인이 발생 할 수 있다. 따라서 List 형태로 리턴한다.
## 3. 사용자 추가
+ 전체 조건
    + 사용자 추가는 [분기 조건]과 같이 분기되며, 1개의 API 에서 동작해야 한다.
    + csv , Json 형태의 입력에서 전화번호, 날짜형식은 서로 상이하다.
        + json : tel : 010-7531-2468, joined :2013-07-01
        + csv : tel : 01075312468, joined : 2018.03.07
    + 전화번호와 날짜는 다음과 같은 형식으로 API 내부에서 통일시켜 요구사항의 출력에 맞게 변경한다.
        + 전화번호 : 010-7531-2468
        + 날짜 : yyyy-MM-dd
    + 모든 응답은 HttpStatuc Code 기반으로 한다


+ 분기 조건
    + HTTP Body 가 Json 일 경우
    + HTTP Body 가 csv 일 경우
    + csv 형태의 파일이 첨부 될 경우
    + json 형태의 파일이 첨부 될 경우


# 4. 프로젝트 패키지 설명
## confg-aop
+ 각종 설정
    + controllerAdvice : ControllerAdvice 를 이용한 유효성 검증, 여기서는 전화번호를 정규식을 이용해 검증한다.
    + validation : 전화번호를 010-3535-7979 형태에 맞도록 수정한다.
## config-DummyLoader
 + 프로젝트 기동시 H2 DB에 더미 데이터를 삽입한다 (테스트 용도)
## controllers
 + REST API 컨트롤러
## entity
 + 사용자 정보를 저장하는 entity 객체 패키지
## exception
 + ExceptionHandler 를 이용한 커스텀 예외처리 설정
## utils
 + Csv 파일과 Json 관련된 유틸리티 Static 클래스

# 5. 실행방법 구성
## 5.1 Java -jar 를 이용한 빌드
 + 프로젝트 루트 경로의 contact-0.0.1-SNAPSHOT.jar 를 실행
 ```
 java -jar contact-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
 ```

## 5.2 Terraform을 통한 AWS EC2 배포 후 실행
 + 목표 : AWS Ec2에 도커 이미지를 배포하고 이를 Terraform 으로 구성
 + 흐름
    + 빌드된 Docker 파일을 ECR 에 배포
    + Terraform을 통해 EC2 생성 후 생성된 EC2에 ECR 이미지 Pull 후 어플리케이션 실행
 + 조건 : AWs CLI 설치 후 로컬 머신에 Credential 이 적용되어 있어야 한다
 + 스크립트는 terraform/initEc2.tf 참고
#### ECR 구성
 1. ECR Repository 구성
    ~~~
    aws ecr create-repository --repository-name clo-task-spring-boot-app
    ~~~

2. ECR Login
    ~~~
    aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 818729859049.dkr.ecr.ap-northeast-2.amazonaws.com/clo-task-spring-boot-app:latest
    ~~~

3. Docker 빌드
    ~~~
    docker build -t clo-task-spring-boot-app:latest --no-cache .
    ~~~

4. Docker Image Tag 추가
    ~~~
    docker tag clo-task-spring-boot-app 818729859049.dkr.ecr.ap-northeast-2.amazonaws.com/clo-task-spring-boot-app:latest
    ~~~

5. Docker Image ECR Push
    ~~~
    docker push 818729859049.dkr.ecr.ap-northeast-2.amazonaws.com/clo-task-spring-boot-app:latest
    ~~~
#### Terraform 을 통한 EC2 구성 및 ECR 이미지 Pull 스크립트
```
provider "aws" {
    region = "ap-northeast-2"
}

resource "aws_security_group" "ssh_access" { #보안 그룹 생성
  name        = "ssh-access"
  description = "Devlopment for Allow SSH access from anywhere"

  # 인바운드 규칙 설정: SSH 접속 허용
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  // 모든 IP에서 접속 허용
  }

  # API Port
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # 이 보안 그룹을 EC2 인스턴스에 연결
  tags = {
    Name = "SSH Access"
  }
}

#EC2가 ECR에 접근할 수 있는 IAM 역할 생성
resource "aws_iam_role" "ec2_role" {
  name = "ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      },
    ]
  })
}

#EC2에 ec2-role 역할 부여 정책 생성
resource "aws_iam_role_policy" "ecr_policy" {
  name = "ecr-policy"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}

#정책을 사용할 수 있도록 프로파일 생성
resource "aws_iam_instance_profile" "ec2_instance_profile" {
  name = "ec2-instance-profile"
  role = aws_iam_role.ec2_role.name
}

resource "aws_instance" "clo_task" { #ec2 인스턴스 정보
    ami = "ami-0e2d37cc316c4d801" #AWS Linux
    instance_type = "t4g.nano"
    key_name = "task-key-pair"  #적용할 키페어
    iam_instance_profile = aws_iam_instance_profile.ec2_instance_profile.name # 인스턴스에 ec2_instance_profile 부여

    #보안 그룹 연결
    vpc_security_group_ids = [aws_security_group.ssh_access.id]
    depends_on = [aws_security_group.ssh_access] #인스턴스 생성후 보안그룹에 연결한다.

    user_data = <<-EOF
    #!/bin/bash
    sudo dnf update -y
    sudo dnf install docker -y
    sudo systemctl start docker
    sudo usermod -aG docker ec2-user
    sudo systemctl enable docker

    # 로그 파일에 기록
    exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1

    echo "Logging into ECR..."
    # AWS 자격 증명을 IAM 역할에서 가져옴
    $(aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 818729859049.dkr.ecr.ap-northeast-2.amazonaws.com)

    echo "Pulling Docker image..."
    docker pull 818729859049.dkr.ecr.ap-northeast-2.amazonaws.com/clo-task-spring-boot-app:latest

    echo "Running Docker container..."
    docker run -d -p 8080:8080 --platform linux/arm64/v8 818729859049.dkr.ecr.ap-northeast-2.amazonaws.com/clo-task-spring-boot-app:latest
    

    echo "User data script completed."
    EOF

    tags = {
        Name = "Test Ec2"
    }
}

resource "aws_eip" "clo_task_eip" { # EIP 구성
  instance = aws_instance.clo_task.id
  depends_on = [aws_instance.clo_task] #인스턴스가 생성된 후  EIP에 연결한다.
}

output "instance_ip" {
  value = aws_eip.clo_task_eip.public_ip
}
```