package com.mycompany.dvdstore.core.repository.file;

import com.mycompany.dvdstore.core.entity.Movie;
import com.mycompany.dvdstore.core.repository.MovieRepositoryInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileMovieRepository implements MovieRepositoryInterface {

    @Value("${movies.file.location}")
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void add(Movie movie) {

        long lastId=list().stream().map(Movie::getId).max(Long::compare).orElse(0L);
        movie.setId(lastId+1);
        /*long lastId = list.stream().mapToLong(Movie::getId).max().orElse(0L);
        movie.setId(lastId + 1);*/

        FileWriter writer;
        try {
            writer= new FileWriter(file, true);
            writer.write(movie.getId()+";"+ movie.getTitle() + ";" + movie.getGenre()+";"+movie.getSummary());
            writer.write("\n");
            writer.close();
            //writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The movie " + movie.getTitle() + " has been added.");
    }

    @Override
    public List<Movie> list() {
        List<Movie> movies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                final Movie movie = new Movie();
                final String[] titreEtGenre = line.split("\\;");
                movie.setId(Long.parseLong(titreEtGenre[0]));
                movie.setTitle(titreEtGenre[1]);
                movie.setGenre(titreEtGenre[2]);
                movies.add(movie);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public Movie getById(Long id) {
        final Movie movie = new Movie();
        movie.setId(id);
        try(BufferedReader br =new BufferedReader(new FileReader(file))){
            for(String line; (line=br.readLine()) !=null;){
                final String[] allProperties = line.split("\\;");
                final long nextMovieId=Long.parseLong(allProperties[0]);
                if(nextMovieId==id){
                    movie.setTitle(allProperties[1]);
                    movie.setGenre(allProperties[2]);
                    movie.setSummary(allProperties[3]);
                    return movie;
                }
            }
        }
        catch (FileNotFoundException e){e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}
        catch (NumberFormatException e){System.err.println("A movie from the file does not have a proper id");e.printStackTrace();
        }
        movie.setTitle("UNKNOW");
        movie.setGenre("UNKNOW");
        movie.setSummary("UNKNOW");
        return movie;
    }
}
