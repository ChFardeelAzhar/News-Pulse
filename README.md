## 📖 API Integration Documentation

### 📰 News API Integration
This project fetches news articles from an external API and displays them in the app.

### 📍 API Source:
- We use the **[News API](https://newsapi.org/)** to fetch real-time news.

### 📡 API Endpoints Used:
| Endpoint        | Description                             | Example URL |
|----------------|-----------------------------------------|-------------|
| `/top-headlines` | Fetches the latest headlines from various sources. | `https://newsapi.org/v2/top-headlines?country=us&apiKey=YOUR_API_KEY` |

### 🔑 API Authentication:
- An API key is required to make requests.
- Sign up at [News API](https://newsapi.org/) to get your free API key.

### 📥 Example API Request:
To fetch top headlines:
```kotlin
val apiKey = "YOUR_API_KEY"
val url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=$apiKey"

// Making API call using Retrofit
@GET("top-headlines")
suspend fun getTopHeadlines(
    @Query("country") country: String,
    @Query("apiKey") apiKey: String
): Response<NewsResponse>

📤 Example API Response (JSON):
{
  "status": "ok",
  "totalResults": 38,
  "articles": [
    {
      "source": { "id": "bbc-news", "name": "BBC News" },
      "author": "BBC News",
      "title": "Breaking News Headline",
      "description": "This is a sample news description.",
      "url": "https://www.bbc.com/news",
      "urlToImage": "https://example.com/image.jpg",
      "publishedAt": "2024-06-01T12:00:00Z"
    }
  ]
}

⚠️ Error Handling:
401 Unauthorized: Invalid API key → Ensure you use a valid key.
429 Too Many Requests: API rate limit exceeded → Reduce requests or upgrade your plan.
500 Internal Server Error: API issue → Try again later.
📌 How API is Used in the Project:
The API is called using Retrofit inside the repository.
The response is processed and displayed in the UI.

