FROM nginx:alpine
COPY ./nginx/etc/. /etc

ENTRYPOINT ["nginx","-c","/etc/nginx/nginx.conf"]