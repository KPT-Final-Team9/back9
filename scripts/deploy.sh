IS_DEV1=$(docker ps | grep back9-dev1)
IS_DEV2=$(docker ps | grep back9-dev2)
HEALTH_CHECK_REQUEST_NGINX=$(bash -c '</dev/tcp/127.0.0.1/80 >/dev/null && echo "Connected" || true')

NGINX_RUNNING=$(docker ps -q -f name=nginx)
DEFAULT_CONF=" /etc/nginx/nginx.conf"

if [ -z "$NGINX_RUNNING" ]; then
    echo "Nginx 컨테이너가 실행 중이 아닙니다. nginx 컨테이너를 실행시킵니다."

    # Docker Compose 실행
    docker-compose up -d nginx certbot

    if [ "$HEALTH_CHECK_REQUEST_NGINX" = "Connected" ]; then
            echo "Nginx 컨테이너가 성공적으로 실행되었습니다. [ CONTAINER ID ] : $NGINX_RUNNING"
        else
            echo "Nginx 컨테이너 실행에 실패했습니다... 로그를 확인하세요."
            exit 1
    fi

else
  echo "Nginx 컨테이너가 이미 실행 중입니다."

fi

CURRENT_SERVER_PORT=$(docker exec nginx grep -o 'proxy_pass http://[^:]\+:[0-9]\+' /etc/nginx/nginx.conf | awk -F ':' '{print $NF}' | head -n1)
if [ "$CURRENT_SERVER_PORT" = "8082" -o -z "$IS_DEV1" ];then # dev2운영중 or 첫 배포 (환경변수로 설정한 문자열 길이가 0인 경우 -z)

  if [ "$IS_DEV1" ];then
    echo "down old container (dev1)"
    sudo docker-compose stop back9-dev1
    sudo docker-compose rm -f back9-dev1 # 신버전 반영 위해 기존 컨테이너 down 처리
  fi

  echo "##### dev2 => dev1 #####"

  echo "1. get update version image"
  sudo docker-compose pull back9-dev1 # dev1으로 이미지를 내려받아옴

  echo "2. update version container up"
  sudo docker-compose up -d back9-dev1 # dev1 컨테이너 실행

  counter=0
  while [ 1 = 1 ]; do
  echo "3. health check 진행 중..."
  ((counter++))
  sleep 3

    HEALTH_CHECK_REQUEST_DEV1=$(curl -s http://172.18.0.1:8082/public-api/health | grep -o '"status":"[^"]*' | awk -F '"' '{print $4}')
    if [ "$HEALTH_CHECK_REQUEST_DEV1" = "Connected" ]; then # 서비스 가능하면 health check 중지 (문자열 길이가 0보다 큰지 판단 -n)
      echo "health check 성공 !"
      echo "시도 횟수 : $counter"
      break ;
    fi
  done;

  echo "4. reload nginx"
  docker exec nginx cp /etc/nginx/nginx.green.conf $DEFAULT_CONF
  docker exec nginx nginx -s reload

  echo "5. deploy check new version"

#  CURRENT_SERVER_PORT=$(docker exec nginx grep -o 'proxy_pass http://[^:]\+:[0-9]\+' /etc/nginx/nginx.conf | awk -F ':' '{print $NF}' | head -n1)
  CURRENT_SERVER_PORT=$(curl -s http://172.18.0.1:8081/public-api/health | grep -o '"status":"[^"]*' | awk -F '"' '{print $4}')
  if [ "$CURRENT_SERVER_PORT" = "Connected" ];then
    echo "dev1 서버가 성공적으로 배포되었습니다! [ CURRENT_SERVER_PORT ] : $CURRENT_SERVER_PORT"
    /home/ubuntu/app/alarm.sh
  fi

else # dev2 운영중인 경우

  if [ -n "$IS_DEV2" ];then
    echo "down old container (dev2)"
    sudo docker-compose stop back9-dev2
    sudo docker-compose rm -f back9-dev2 # 신버전 반영 위해 기존 컨테이너 down 처리
  fi
  echo "### dev1 => dev2 ###"

  echo "1. get update version image"
  sudo docker-compose pull back9-dev2

  echo "2. update version container up"
  sudo docker-compose up -d back9-dev2

  counter=0
  while [ 1 = 1 ]; do
    echo "3. health check 진행 중..."
    ((counter++))
    sleep 3

    HEALTH_CHECK_REQUEST_DEV2=$(curl -s http://172.18.0.1:8082/public-api/health | grep -o '"status":"[^"]*' | awk -F '"' '{print $4}')
    if [ "$HEALTH_CHECK_REQUEST_DEV2" = "Connected" ]; then # 서비스 가능하면 health check 중지 (문자열 길이가 0보다 큰지 판단 -n)
      echo "health check 성공!"
      echo "시도 횟수 : $counter"
      break ;
    fi
  done;

  echo "4. reload nginx"
  docker exec nginx cp /etc/nginx/nginx.blue.conf $DEFAULT_CONF
  docker exec nginx nginx -s reload

  echo "5. deploy check new version"

#  CURRENT_SERVER_PORT=$(docker exec nginx grep -o 'proxy_pass http://[^:]\+:[0-9]\+' /etc/nginx/nginx.conf | awk -F ':' '{print $NF}' | head -n1)
  CURRENT_SERVER_PORT=$(curl -s http://172.18.0.1:8082/public-api/health | grep -o '"status":"[^"]*' | awk -F '"' '{print $4}')
  if [ "$CURRENT_SERVER_PORT" = "Connected" ];then
    echo "dev2 서버가 성공적으로 배포되었습니다! [ CURRENT_SERVER_PORT ] : $CURRENT_SERVER_PORT"
    /home/ubuntu/app/alarm.sh
  fi

fi

