# Development image: Vite dev server with hot reload
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
# npm arbortist bug with this lockfile; resolve fresh for the dev image
RUN npm install --legacy-peer-deps
COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
