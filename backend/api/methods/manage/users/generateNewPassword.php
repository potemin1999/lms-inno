<?php

    // If access token is invalid or if user doesn't have an access to this method then exit with error
    ensure_access(array(-1, 0)); // Only librarians are allowed to use this method

    // If some of the required parameters are missed then exit with error
    // If some parameter has invalid value then exit with error
    ensure_required_params(array('id'), $_POST);

    if(in_array(get_user_type($_POST['id']), array(0))) {
        // Only administrators are allowed
        if(get_user_type() != -1) {
            json_response(401, array('errorMessage' => 'User associated with given Access-Token doesn\'t have permission to modify librarians'));
        }
    } else {
        // Only librarians are allowed
        if(get_user_type() != 0) {
            json_response(401, array('errorMessage' => 'User associated with given Access-Token doesn\'t have permission to modify users'));
        }
        ensure_permissions(array('modify' => 1)); // Check librarian's permissions
    }

    // If user doesn't exist then exit with error
    $query = $db->prepare("SELECT * FROM users WHERE id = ?");
    $query->bind_param("i", $_POST['id']);
    $query->execute();
    if ($query->get_result()->fetch_assoc() === null) {
        json_response(400, array('errorMessage' => 'User with given id doesn\'t exist'));
    }
    unset($query);

    $response = array('id' => $_POST['id'], 'password' => generate_password());

    $query = $db->prepare("UPDATE users SET password = ? WHERE id = ?");
    $query->bind_param("si", hash_password($response['password']), $_POST['id']);
    $query->execute();
    unset($query);

    json_response(200, $response);

?>
