package uz.ttpu.movieshelf.data.mapper

import uz.ttpu.movieshelf.data.remote.MovieDto
import uz.ttpu.movieshelf.domain.model.Movie

// The mapper belongs to the data layer and not to the domain because
// the domain must not know about DTOs. The DTO is a data-layer concept
// (it mirrors the server's JSON structure), so the conversion logic
// stays here, at the boundary between data and domain.

fun MovieDto.toDomain(isFavorite: Boolean): Movie = Movie(
    id = id,
    title = title,
    year = releaseYear,
    rating = score,
    isFavorite = isFavorite,
)
