# --- STAGE 1: Build ---
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build -- --configuration production

# --- STAGE 2: Serve ---
FROM nginx:stable-alpine
COPY --from=build /app/dist/app1/browser/ /usr/share/nginx/html/
RUN mv /usr/share/nginx/html/index.csr.html /usr/share/nginx/html/index.html 2>/dev/null || true
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]