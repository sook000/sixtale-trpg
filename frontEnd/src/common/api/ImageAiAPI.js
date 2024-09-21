import axios from "axios";

// 환경 변수에서 API 키를 가져옵니다.
const OPENAI_API_KEY = process.env.VUE_APP_OPENAI_API_KEY;
const OPENAI_BASE_URL = "https://api.openai.com/v1/images/generations";
const CHAT_API_URL = "https://api.openai.com/v1/chat/completions";

// 텍스트를 번역하는 함수
const translateText = async (text) => {
  const messages = [
    {
      role: "system",
      content: "You are a helpful assistant that translates Korean to English.",
    },
    {
      role: "user",
      content: `Translate the following Korean text to English: "${text}"`,
    },
  ];

  try {
    const response = await axios.post(
      CHAT_API_URL,
      {
        model: "gpt-3.5-turbo",
        messages: messages,
        max_tokens: 100,
        temperature: 0.7,
      },
      {
        headers: {
          Authorization: `Bearer ${OPENAI_API_KEY}`,
          "Content-Type": "application/json",
        },
      }
    );

    if (response.data && response.data.choices && response.data.choices.length > 0) {
      return response.data.choices[0].message.content.trim();
    } else {
      throw new Error("Translation failed: Invalid response structure");
    }
  } catch (error) {
    console.error("Error translating text:", error);
    throw error;
  }
};

// 이미지를 생성하는 함수
const generateImage = async (description) => {
  // 불필요한 공백 및 특수 문자를 제거하여 프롬프트를 정리합니다.
  const cleanedDescription = description.replace(/[^\w\s,]/g, "").trim();
  const fullPrompt = `Portrait of a fantasy game animation character: ${cleanedDescription}. The character is emphasized with a plain background similar to a passport photo.`;
  console.log("Full Prompt:", fullPrompt);

  const data = JSON.stringify({
    "prompt": fullPrompt,
    "n": 1,
    "size": "1024x1024"
  });

  const config = {
    method: 'post',
    url: OPENAI_BASE_URL,
    headers: { 
      'Authorization': `Bearer ${OPENAI_API_KEY}`, 
      'Content-Type': 'application/json',
    },
    data: data
  };

  try {
    const response = await axios(config);

    if (response.data && response.data.data && response.data.data.length > 0) {
      const imageUrl = response.data.data[0].url;
      console.log("Generated Image URL:", imageUrl);
      return imageUrl;
    } else {
      throw new Error("Image generation failed: Invalid response structure");
    }
  } catch (error) {
    console.error("Error generating image:", error.response ? error.response.data : error.message);
    throw error;
  }
};

// 번역과 이미지 생성 통합 함수
export const handleGenerateImage = async (description) => {
  try {
    const translatedDescription = await translateText(description);
    return await generateImage(translatedDescription);
  } catch (error) {
    console.error("Error in handleGenerateImage:", error);
    throw error;
  }
};
