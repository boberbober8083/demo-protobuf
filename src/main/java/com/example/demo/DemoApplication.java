package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DemoApplication {

	public static void main(String[] args) {
		run();
	}

	public static void run() {
		Protos.GetBookResponse.Builder builder = Protos.GetBookResponse.newBuilder();
		List<Book> messages = new ArrayList<>();

		for (int i = 0; i < 1000; ++i) {
			long isbn = System.nanoTime();
			String author = "Tolstoy " + i;
			String title = "Super book " + i;

			Protos.Book.Builder bookBuilder = Protos.Book.newBuilder();
			bookBuilder.setAuthor(author);
			bookBuilder.setIsbn(isbn);
			bookBuilder.setTitle(title);
			builder.addBook(bookBuilder);

			Book book = new Book();
			book.setAuthor(author);
			book.setIsbn(isbn);
			book.setTitle(title);

			messages.add(book);
		}

		Message message = new Message(messages);

		Protos.GetBookResponse response = builder.build();
		int protoSize = 0;

		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			com.google.protobuf.CodedOutputStream coded = com.google.protobuf.CodedOutputStream.newInstance(stream);
			response.writeTo(coded);
			coded.flush();
			byte[] bytes = stream.toByteArray();
			protoSize = bytes.length;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		int jsonSize = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			// Converting the Java object into a JSON string
			String jsonStr = objectMapper.writeValueAsString(message);
			jsonSize = jsonStr.length();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.out.printf("Ratio - (protobuf - %d) / (json - %d)",
				protoSize, jsonSize);
	}

}
