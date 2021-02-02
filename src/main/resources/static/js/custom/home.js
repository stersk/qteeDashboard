
window.onload = function() {
    // Potentially didn't work at IE, Opera mini,
    const url = new URL(location);
    if (url.searchParams.has('login')) {
        url.searchParams.delete('login');
        history.replaceState(null, null, url);

        $('#loginModal').modal('toggle');
    }
};