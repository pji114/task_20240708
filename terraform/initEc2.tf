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