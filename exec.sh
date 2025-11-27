cat <<EOF >request.json
{
    "contents": [
        {
            "role": "user",
            "parts": [
                {
                  "fileData": {
                    "mimeType": "image/jpeg",
                    "fileUri": "gs://cloud-samples-data/generative-ai/image/woman.jpg"
                  }
                },
                {
                  "fileData": {
                    "mimeType": "image/png",
                    "fileUri": "gs://cloud-samples-data/generative-ai/image/suitcase.png"
                  }
                },
                {
                  "fileData": {
                    "mimeType": "image/png",
                    "fileUri": "gs://cloud-samples-data/generative-ai/image/armchair.png"
                  }
                },
                {
                  "fileData": {
                    "mimeType": "image/png",
                    "fileUri": "gs://cloud-samples-data/generative-ai/image/man-in-field.png"
                  }
                },
                {
                  "fileData": {
                    "mimeType": "image/jpeg",
                    "fileUri": "gs://cloud-samples-data/generative-ai/image/shoes.jpg"
                  }
                },
                {
                  "fileData": {
                    "mimeType": "image/png",
                    "fileUri": "gs://cloud-samples-data/generative-ai/image/living-room.png"
                  }
                },
                {
                    "text": "Generate an image of a woman sitting in a living room with a man. The man is wearing the brown sneakers. The woman is wearing a red version of the sneakers. The woman is sitting in a white armchair with a blue suitcase next to her."
                }
            ]
        }
    ]
    , "generationConfig": {
        "temperature": 1
        ,"maxOutputTokens": 32768
        ,"responseModalities": ["TEXT", "IMAGE"]
        ,"topP": 0.95
        ,"imageConfig": {
            "aspectRatio": "1:1"
            ,"imageSize": "1K"
            ,"imageOutputOptions": {
                "mimeType": "image/png"
            }
            ,"personGeneration": "ALLOW_ALL"
        }
    },
    "safetySettings": [
        {
            "category": "HARM_CATEGORY_HATE_SPEECH",
            "threshold": "OFF"
        },
        {
            "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
            "threshold": "OFF"
        },
        {
            "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
            "threshold": "OFF"
        },
        {
            "category": "HARM_CATEGORY_HARASSMENT",
            "threshold": "OFF"
        }
    ]
}
EOF

API_KEY="AQ.Ab8RN6IfV2mq72DorNXe2-_B2oMbA0tZIuCfjeEqcMUCawIJvQ"
API_ENDPOINT="aiplatform.googleapis.com"
MODEL_ID="gemini-3-pro-image-preview"
GENERATE_CONTENT_API="streamGenerateContent"

curl \
  -X POST \
  -H "Content-Type: application/json" \
  "https://${API_ENDPOINT}/v1/publishers/google/models/${MODEL_ID}:${GENERATE_CONTENT_API}?key=${API_KEY}" -d '@request.json'
