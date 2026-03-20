<div align="center">
  <h1>🏁 AndefRacing Employee</h1>
  <p>
    <strong>Фронтенд-приложение для сотрудников сети симрейсинг-клубов AndefRacing</strong>
  </p>
  <p>
    <img src="https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black" alt="React 18">
    <img src="https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white" alt="TypeScript">
    <img src="https://img.shields.io/badge/Vite-5-CF4281?logo=vite&logoColor=white" alt="Vite">
    <img src="https://img.shields.io/badge/Tailwind%20CSS-3-06B6D4?logo=tailwind-css&logoColor=white" alt="Tailwind CSS">
  </p>
</div>

## 📋 Описание

Веб-приложение для управления бронированиями, сотрудниками и отчетами в сети симрейсинг-клубов AndefRacing.

## 🚀 Быстрый старт

### Требования

- Node.js >= 18.x
- npm >= 9.x

### Установка

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd A-R-E-F
```

2. Установите зависимости:
```bash
npm install
```

3. Создайте файл `.env` на основе `.env.example`:
```bash
cp .env.example .env
```

4. Настройте переменные окружения в файле `.env` (при необходимости)

### Запуск в режиме разработки

```bash
npm run dev
```

Приложение будет доступно по адресу: http://localhost:3001

### Сборка для продакшена

```bash
npm run build
```

Собранные файлы будут находиться в папке `dist/`

### Предварительный просмотр продакшен-сборки

```bash
npm run preview
```

### Линтинг

```bash
npm run lint
```

## 🛠 Технологии

- **React 18** - UI библиотека
- **TypeScript** - типизация
- **Vite** - сборщик и dev-сервер
- **React Router** - маршрутизация
- **TanStack Query** - управление серверным состоянием
- **Zustand** - управление клиентским состоянием
- **Axios** - HTTP клиент
- **React Hook Form** - работа с формами
- **Tailwind CSS** - стилизация
- **Lucide React** - иконки
- **date-fns** - работа с датами

## 📁 Структура проекта

```
src/
├── api/           # API клиенты
├── components/    # Переиспользуемые компоненты
├── hooks/         # Кастомные хуки
├── lib/           # Библиотеки и утилиты
├── pages/         # Страницы приложения
├── stores/        # Zustand хранилища
├── types/         # TypeScript типы
└── utils/         # Вспомогательные функции
```

## 🔧 Конфигурация

### Прокси API

В режиме разработки все запросы к `/api` проксируются на `http://localhost:8080`. Настройки прокси находятся в `vite.config.ts`.

### Алиасы путей

Настроен алиас `@/` для импорта из папки `src/`:

```typescript
import Component from '@/components/Component'
```

## 🐳 Docker

Для запуска в Docker используйте:

```bash
docker build -t andefracing-employee .
docker run -p 80:80 andefracing-employee
```

## 📝 Лицензия

Proprietary
