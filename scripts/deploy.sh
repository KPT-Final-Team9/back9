IS_DEV1=$(docker ps | grep back9-dev1)
IS_DEV2=$(docker ps | grep back9-dev2)
CURRENT_SERVER_PORT=$(docker exec nginx grep -oP '(?<=proxy_pass http://127.0.0.1:)\d+' /etc/nginx/nginx.conf | head -n1)
DEFAULT_CONF=" /etc/nginx/nginx.conf"

echo "[ CURRENT_SERVER_PORT ] : $CURRENT_SERVER_PORT"
if [ "$CURRENT_SERVER" = "8082" -o -z "$IS_DEV1" ];then # dev2운영중 or 첫 배포 (환경변수로 설정한 문자열 길이가 0인 경우 -z)

  if [ -n "$IS_DEV1" ];then
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

  HEALTH_CHECK_REQUEST=$(bash -c '</dev/tcp/127.0.0.1/8081 >/dev/null && echo "Connected" || true') # dev1으로 request
    if [ -n "$HEALTH_CHECK_REQUEST" ]; then # 서비스 가능하면 health check 중지 (문자열 길이가 0보다 큰지 판단 -n)
      echo "health check 성공 !"
      echo "시도 횟수 : $counter"
      break ;
    fi
  done;

  echo "4. reload nginx"
  docker exec nginx cp /etc/nginx/nginx.green.conf $DEFAULT_CONF
  docker exec nginx nginx -s reload

  echo "5. deploy check new version" # 서버 port 체크
  if [ "$CURRENT_SERVER_PORT" = "Connected" ];then
    echo "dev1 서버가 성공적으로 배포되었습니다 ! [ CURRENT_SERVER_PORT ] : $CURRENT_SERVER_PORT"
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

    HEALTH_CHECK_REQUEST=$(bash -c '</dev/tcp/127.0.0.1/8082 >/dev/null && echo "Connected" || true') # dev2로 request
    if [ "$HEALTH_CHECK_REQUEST" = "Connected" ]; then # 서비스 가능하면 health check 중지 (문자열 길이가 0보다 큰지 판단 -n)
      echo "health check 성공 !"
      echo "시도 횟수 : $counter"
      break ;
    fi
  done;

  echo "4. reload nginx"
  docker exec nginx cp /etc/nginx/nginx.blue.conf $DEFAULT_CONF
  docker exec nginx nginx -s reload

  echo "5. deploy check new version" # 서버 port 체크
  if [ "$CURRENT_SERVER_PORT" = "8082" ];then
    echo "dev2 서버가 성공적으로 배포되었습니다 ! [ CURRENT_SERVER_PORT ] : $CURRENT_SERVER_PORT"
    /home/ubuntu/app/alarm.sh
  fi

fi

